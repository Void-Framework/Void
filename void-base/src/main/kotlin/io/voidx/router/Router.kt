package io.voidx.router

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildRequest
import io.voidx.dto.buildResponse
import io.voidx.dto.headers
import io.voidx.dto.writeHTTP
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.page.*
import io.voidx.router.exceptions.RouteNoTargetException
import io.voidx.router.tree.RouteNode
import io.voidx.util.toResult
import java.io.File
import java.net.Socket
import java.net.URLDecoder
import java.util.jar.JarFile

/**
 * Central registry and dispatcher for routes/pages and middleware.
 *
 * - Holds static, dynamic, and KTS routes.
 * - Manages global middleware execution order and processing.
 * - Serves embedded JS/CSS resources and wires them into page metadata.
 */
class Router {
    private var internalRelay: List<Relay> = emptyList()

    /**
     * Set of global middlewares registered in this router.
     * Higher priority relays run earlier for BEFORE hooks and earlier for AFTER hooks.
     */
    val relay = mutableSetOf<Relay>()

    internal val rootNode = RouteNode()

    companion object {
        val routers = mutableSetOf<Router>()

        /**
         * Parses the query parameters from a raw target string (path[?query]) applying URL decoding.
         * - Keys without values are ignored.
         * - Decoding uses UTF-8 and treats '+' as space per application/x-www-form-urlencoded.
         */
        internal fun parseQuery(rawTarget: String): Map<String, String> {
            val qMark = rawTarget.indexOf('?')
            if (qMark < 0 || qMark + 1 >= rawTarget.length) return emptyMap()
            val qs = rawTarget.substring(qMark + 1)
            val map = LinkedHashMap<String, String>(4)
            var start = 0
            while (start < qs.length) {
                val amp = qs.indexOf('&', start).let { if (it == -1) qs.length else it }
                if (amp > start) {
                    val eq = qs.indexOf('=', start).let { if (it == -1 || it > amp) -1 else it }
                    if (eq != -1) {
                        val rawKey = qs.substring(start, eq)
                        val rawValue = qs.substring(eq + 1, amp)
                        try {
                            val key = URLDecoder.decode(rawKey, Charsets.UTF_8)
                            val value = URLDecoder.decode(rawValue, Charsets.UTF_8)
                            // Skip if decoding produced Unicode replacement characters (indicates malformed percent-encoding)
                            if (key.contains('\uFFFD') || value.contains('\uFFFD')) {
                                // skip malformed pair
                            } else {
                                map[key] = value
                            }
                        } catch (_: Exception) {
                            // Skip malformed encodings
                        }
                    }
                }
                start = amp + 1
            }
            return map
        }
    }

    init {
        recomputeMiddlewareSnapshot()
        routers.add(this)
        // Notify bootstrap modules that a Router is ready.
        Bootstrap.fireRouterCreated(this)
    }

    /** Adds this page to the router using unary plus syntax: +page. */
    fun route(page: Page) {
        addRoute(page)
    }

    /**
     * Convenience operator to add a global middleware to the router: `+MyRelay()`.
     */
    operator fun Relay.unaryPlus() {
        relay.add(this)
        recomputeMiddlewareSnapshot()
    }

    internal fun recomputeMiddlewareSnapshot() {
        internalRelay = relay.sortedByDescending { it.priority }
    }

    internal fun middlewareProcessBefore(requestDTO: Result<RequestDTO>): ResponseDTO? {
        val relays = internalRelay
        for (i in relays.indices) {
            val relay = relays[i]
            val newResponse = (relay as? RelayBefore)?.processBefore(requestDTO)
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
    }

    /**
     * Executes all registered global AFTER middleware with the produced [response].
     */
    fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        val relays = internalRelay
        for (i in relays.indices) {
            (relays[i] as? RelayAfter)?.processAfter(response)
        }
    }

    /**
     * Registers a [route] with the router.
     *
     * - Applies HTML integration hooks (JS/CSS injection) if available.
     * - Routes of special types update router defaults: [ExceptionPage] and [NotFoundPage].
     * - Dynamic pages are indexed by their tokenized path segments for matching.
     *
     * @return this router for chaining.
     */
    fun addRoute(route: Page): Router {
        // Run Bootstrap page decorators (resource wiring, etc.)
        Bootstrap.runPageDecorators(route, this)
        when (route) {
            is ExceptionPage -> {
                exceptionPage = route
            }

            is NotFoundPage -> {
                nullPage = route
            }

            is GroupPage -> {
                if (!route.flattened) {
                    addRoutes(route.flatten())
                } else {
                    if (!route.target.startsWith("/")) throw RouteNoTargetException(route.target)
                    rootNode.insert(route.target.split("/"), 1, route)
                }
            }

            else -> {
                if (!route.target.startsWith("/")) throw RouteNoTargetException(route.target)
                rootNode.insert(route.target.split("/"), 1, route)
            }
        }
        return this
    }

    /**
     * Registers multiple [routes] at once, returning this router for chaining.
     */
    fun addRoutes(routes: List<Page>): Router {
        routes.forEach {
            addRoute(route = it)
        }
        return this
    }

    /**
     * Dispatches an incoming request through middleware and the matched page, then writes the resulting HTTP response to the client's socket.
     *
     * Resolves the target and query from the request, runs global BEFORE middleware (which may short-circuit), gives special/KTS handlers a chance to handle the request, then resolves a static or dynamic page (falling back to the configured 404). For resolved pages the function runs per-page BEFORE (which may short-circuit) and per-page AFTER middleware, runs global AFTER middleware, attaches the original request to the response, and writes the response using the server's HTTP version.
     *
     * @param requestDTO The incoming request data transfer object.
     */
    internal fun route(
        requestDTO: RequestDTO,
        client: Socket,
        version: Number,
    ) {
        try {
            // Request lifecycle events removed; Bootstrap manages explicit hooks only
            val rawTarget = requestDTO.target.removeSuffix(if (requestDTO.target.last() == '/') "/" else "")
            val qMark = rawTarget.indexOf('?')
            val target = if (qMark >= 0) rawTarget.take(qMark) else rawTarget
            val query: Map<String, String> by lazy { parseQuery(rawTarget) }
            var usedPage: Page? = null

            val response =
                middlewareProcessBefore(requestDTO.toResult())
                    ?: Bootstrap.tryHandleSpecialRoute(requestDTO, query)
                    ?: rootNode
                        .match(target.split("/"), 1, mutableMapOf())
                        .also {
                            usedPage = it
                        }?.let { page ->
                            page.middlewareProcessBefore(requestDTO)
                                ?: page.content(requestDTO, query)
                        }
                    ?: nullPage.also { usedPage = it }.content(requestDTO, query)

            // After deciding the response from BEFORE middleware/handler
            response._request = requestDTO

            usedPage?.middlewareProcessAfter(response.toResult())

            middlewareProcessAfter(
                response.toResult(),
            )
            val out = client.getOutputStream()
            out.writeHTTP(
                response,
                version,
            )
        } catch (e: Exception) {
            error(client, e, version, requestDTO)
        }
    }

    /**
     * Sends an error response using the configured [ExceptionPage] when an exception [e]
     * occurs during request handling for the given [client].
     */
    internal fun error(
        client: Socket,
        e: Exception,
        version: Number,
        request: RequestDTO? = null,
    ) {
        // Invoke Bootstrap error handlers; request is unknown at this point
        Bootstrap.fireError(null, e)
        val exPage = exceptionPage
        client.getOutputStream().writeHTTP(
            response =
                exPage.content(
                    (request ?: buildRequest { }).apply {
                        attributes["exception"] = e
                    },
                    emptyMap(),
                ),
            version = version,
        )
    }

    /**
     * Returns a [PageHandler] for the static [path], creating and registering one
     * if it does not exist yet. Allows fluent per-method handlers, e.g.:
     * router.on("/api").GET { ... }
     */
    fun route(
        path: String,
        builder: PageHandler.() -> Unit,
    ) {
        rootNode.match(path.split("/"), 1, mutableMapOf())?.let {
            (it as? PageHandler)?.builder()
        } ?: run {
            val page = PageHandler(path)
            addRoute(page)
            page.builder()
        }
    }

    private fun escapeHtml(input: String?): String {
        if (input == null) return ""
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    /**
     * Replace with your real flag if already exists elsewhere.
     */
    var isDebugMode = false

    /**
     * The default page to display when an unhandled exception occurs.
     */
    internal var exceptionPage: ExceptionPage =
        exceptionPage { req, queries, ex ->
            return@exceptionPage buildResponse {
                status = 500
                statusText = "Server Error"
                headers {
                    put("Content-Type", "text/html")
                    put("Connection", "close")
                }
                body = "<!doctype html><html>" +
                    "<head>" +
                    "  <style>" +
                    "#__next-dev-overlay {\n" +
                    "  position: fixed;\n" +
                    "  top: 0;\n" +
                    "  left: 0;\n" +
                    "  width: 100%;\n" +
                    "  height: 100%;\n" +
                    "  background: rgba(0, 0, 0, 0.8);\n" +
                    "  color: #fff;\n" +
                    "  font-family: system-ui, sans-serif;\n" +
                    "  z-index: 2147483647; /* Ensures it stays on top */\n" +
                    "}\n" +
                    "\n" +
                    "/* Main overlay styling */\n" +
                    ".overlay {\n" +
                    "  max-width: 800px;\n" +
                    "  margin: 50px auto;\n" +
                    "  background: #1e1e1e;\n" +
                    "  padding: 20px;\n" +
                    "  border-radius: 4px;\n" +
                    "  box-shadow: 0 2px 10px rgba(0,0,0,0.3);\n" +
                    "}\n" +
                    "\n" +
                    "/* Header section with title and close button */\n" +
                    ".overlay__header {\n" +
                    "  display: flex;\n" +
                    "  justify-content: space-between;\n" +
                    "  align-items: center;\n" +
                    "  margin-bottom: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".overlay__title {\n" +
                    "  font-size: 1.5em;\n" +
                    "  font-weight: bold;\n" +
                    "}\n" +
                    "\n" +
                    ".overlay__close {\n" +
                    "  background: transparent;\n" +
                    "  border: none;\n" +
                    "  font-size: 1.5em;\n" +
                    "  color: #fff;\n" +
                    "  cursor: pointer;\n" +
                    "}\n" +
                    "\n" +
                    "/* Styling for the error message and stack trace */\n" +
                    ".overlay__content {\n" +
                    "  color: #FF4C4C;\n" +
                    "}\n" +
                    ".error-message pre,\n" +
                    ".stack-trace pre {\n" +
                    "  margin: 0;\n" +
                    "  padding: 10px;\n" +
                    "  overflow: auto;\n" +
                    "  background: #2d2d2d;\n" +
                    "  border-radius: 4px;\n" +
                    "  font-size: 0.9em;\n" +
                    "}" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "<div id=\"__next-dev-overlay\">\n" +
                    "  <div class=\"overlay\">\n" +
                    "    <div class=\"overlay__header\">\n" +
                    "      <span class=\"overlay__title\">${escapeHtml(ex::class.simpleName)}: ${escapeHtml(ex.message)}</span>\n" +
                    "    </div>\n" +
                    "    <div class=\"overlay__content\">\n" +
                    "      <div class=\"error-message\">\n" +
                    "        <pre>${escapeHtml(ex::class.simpleName)}: ${escapeHtml(ex.message)}</pre>\n" +
                    "      </div>\n" +
                    "      <div class=\"stack-trace\">\n" +
                    "        <pre>\n" +
                    if (isDebugMode) {
                        ex.stackTrace.joinToString("\n") { escapeHtml(it.toString()) }
                    } else {
                        "Stack trace hidden"
                    }
                "        </pre>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>" +
                    "</body>" +
                    "</html>"
            }
        }

    /**
     * The default page to display when no route matches the request.
     */
    internal var nullPage: NotFoundPage =
        notFoundPage { req, _ ->
            return@notFoundPage buildResponse {
                status = 404
                statusText = "Not Found"
                headers {
                    put("Content-Type", "text/html")
                    put("Connection", "close")
                }
                body =
                    """
                    <!doctype html>
                    <html lang="en">
                    <head>
                        <meta charset="utf-8">
                        <title>404 | Page Not Found</title>
                        <style>
                            body {
                                margin: 0;
                                height: 100vh;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                                font-family: system-ui, sans-serif;
                                background: #0f0f0f;
                                color: #e5e5e5;
                            }
                            .container {
                                text-align: center;
                                padding: 2rem;
                                max-width: 600px;
                            }
                            h1 {
                                font-size: 5rem;
                                margin-bottom: 0.5rem;
                                color: #ff5555;
                            }
                            p {
                                margin: 0.5rem 0 1.5rem;
                                color: #aaa;
                            }
                            a {
                                color: #61dafb;
                                text-decoration: none;
                                font-weight: 600;
                                border: 1px solid #61dafb;
                                border-radius: 6px;
                                padding: 0.5rem 1rem;
                                transition: 0.2s;
                            }
                            a:hover {
                                background: #61dafb;
                                color: #0f0f0f;
                            }
                            .path {
                                font-size: 0.85rem;
                                opacity: 0.7;
                                margin-top: 1rem;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <h1>404</h1>
                            <p>The page you're looking for could not be found.</p>
                            <a href="/">Return Home</a>
                            <div class="path">Requested: ${escapeHtml(req.target)}</div>
                        </div>
                    </body>
                    </html>
                    """.trimIndent()
            }
        }
}

/**
 * Lists resource file paths under the given classpath [folder].
 * Supports running from the filesystem during development or from a JAR at runtime.
 */
fun listResourcePaths(folder: String): List<String> {
    val cl = Thread.currentThread().contextClassLoader
    val url = cl.getResource(folder) ?: return emptyList()
    return when (url.protocol) {
        "file" -> {
            val root = File(url.toURI())
            root
                .walkTopDown()
                .filter { it.isFile }
                .map { "$folder/" + it.relativeTo(root).invariantSeparatorsPath }
                .toList()
        }

        "jar" -> {
            val path = url.path
            val jarPath = path.substringAfter("file:").substringBefore("!")
            JarFile(URLDecoder.decode(jarPath, "UTF-8")).use { jar ->
                jar
                    .entries()
                    .asSequence()
                    .map { it.name }
                    .filter {
                        it.startsWith("$folder/") && !it.endsWith("/")
                    }.toList()
            }
        }

        else -> {
            emptyList()
        }
    }
}

/**
 * Create a Router and apply the given configuration block to it.
 *
 * @param builder Configuration block invoked on the newly created Router.
 * @return The configured Router instance.
 */
fun router(builder: Router.() -> Unit): Router {
    val router = Router()
    router.builder()
    return router
}
