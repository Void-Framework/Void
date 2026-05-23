package io.voidx.router

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.*
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

    /**
     * Executes global BEFORE middleware in priority order and returns the first produced response.
     *
     * Iterates the router's snapshot of relays and invokes `processBefore` on those that implement `RelayBefore`.
     *
     * @param requestDTO The incoming request wrapped as a `Result<RequestDTO>` passed to middleware.
     * @return A `ResponseDTO` returned by the first middleware that short-circuits the request, or `null` if none did.
     */
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

            is BadRequestPage -> {
                badRequestPage = route
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
     * Dispatches a request through global middleware, special handlers, and the route tree, then writes the HTTP response to the client's socket.
     *
     * The response is resolved in this order: global BEFORE middleware (may short-circuit), Bootstrap special/KTS handlers, matched route handler, or the configured 404 page. The original request is attached to the response; per-page AFTER middleware (if a page was selected) and global AFTER middleware are executed before the response is written.
     *
     * @param requestDTO Incoming request data transfer object.
     * @param client Client TCP socket to which the HTTP response will be written.
     * @param version HTTP version number used when writing the response.
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
            val pathParams = mutableMapOf<String, String>()

            val response =
                middlewareProcessBefore(requestDTO.toResult())
                    ?: Bootstrap.tryHandleSpecialRoute(requestDTO, query + pathParams)
                    ?: rootNode
                        .match(target.split("/"), 1, pathParams)
                        .also {
                            usedPage = it
                        }?.let { page ->
                            page.middlewareProcessBefore(requestDTO)
                                ?: page.content(requestDTO, query + pathParams)
                        }
                    ?: nullPage.also { usedPage = it }.content(requestDTO, query + pathParams)

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
     * Retrieves the PageHandler for the given static path, creating and registering one if none exists.
     *
     * @param path The static route path (e.g., "/api").
     * @param builder A configuration block applied to the retrieved or newly created PageHandler.
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
                    put("Content-Type", "application/json")
                    put("Connection", "close")
                }

                body =
                    """
                    {
                      "success": false,
                      "error": {
                        "code": 500,
                        "type": "${escapeHtml(ex::class.simpleName ?: "Exception")}",
                        "message": "${escapeHtml(ex.message ?: "Unknown server error")}",
                        "path": "${escapeHtml(req.target)}",
                        "stackTrace": ${
                        if (isDebugMode) {
                            """
                            [
                              ${
                                ex.stackTrace.joinToString(",\n") {
                                    "\"${escapeHtml(it.toString())}\""
                                }
                            }
                            ]
                            """.trimIndent()
                        } else {
                            "null"
                        }
                    }
                      }
                    }
                    """.trimIndent()
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
                    put("Content-Type", "application/json")
                    put("Connection", "close")
                }

                body =
                    """
                    {
                      "success": false,
                      "error": {
                        "code": 404,
                        "type": "NotFound",
                        "message": "The requested resource could not be found.",
                        "path": "${escapeHtml(req.target)}"
                      }
                    }
                    """.trimIndent()
            }
        }

    internal var badRequestPage: BadRequestPage =
        badRequestPage { req, queries ->

            return@badRequestPage buildResponse {
                status = 400
                statusText = "Bad Request"

                headers {
                    put("Content-Type", "application/json")
                    put("Connection", "close")
                }

                body =
                    """
                    {
                      "success": false,
                      "error": {
                        "code": 400,
                        "type": "BadRequest",
                        "message": "The request could not be understood or was missing required parameters.",
                        "path": "${escapeHtml(req.target)}"
                      }
                    }
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
