package io.voidx.router

import io.voidx.ClientHandler
import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.writeHTTP
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.page.*
import io.voidx.router.util.RequestHandler
import io.voidx.router.util.RouteCheck
import io.voidx.util.toResult
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

/**
 * Central registry and dispatcher for routes/pages and middleware.
 *
 * - Holds static, dynamic, and KTS routes.
 * - Manages global middleware execution order and processing.
 * - Serves embedded JS/CSS resources and wires them into page metadata.
 */
class Router :
    RouteCheck,
    RequestHandler {
    private var internalRelay: List<Relay> = emptyList()
    val relay = mutableSetOf<Relay>()

    val routes: ConcurrentHashMap<String, Page> = ConcurrentHashMap()
    override val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage> = ConcurrentHashMap()

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

    operator fun Relay.unaryPlus() {
        relay.add(this)
        recomputeMiddlewareSnapshot()
    }

    private fun recomputeMiddlewareSnapshot() {
        internalRelay = relay.sortedByDescending { it.priority }
    }

    internal fun middlewareProcessBefore(requestDTO: Result<RequestDTO>): ResponseDTO? {
        val relays = internalRelay
        for (i in 0 until relays.size) {
            val relay = relays[i]
            val newResponse = (relay as? RelayBefore)?.processBefore(requestDTO)
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
    }

    fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        val relays = internalRelay
        for (i in 0 until relays.size) {
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
                RouteCheck.exceptionPage = route
            }

            is NotFoundPage -> {
                RouteCheck.nullPage = route
            }

            is PageHandler -> {
                if (route.target.contains("\\{[^}]+}".toRegex())) {
                    val target = route.target.split("/")
                    dynamicRoutes[target] = route
                } else {
                    handleTargetChecking(route, routes)
                }
            }

            is DynamicPage -> {
                val target = route.target.split("/")
                dynamicRoutes[target] = route
            }

            else -> {
                handleTargetChecking(route, routes)
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
     * Dispatches an incoming [requestDTO] through middleware and the matched page, then
     * writes the response to the [clientHandler]'s socket.
     *
     * Order of operations:
     * 1. Global BEFORE middleware
     * 2. KTS handler if request contains the header "KTS-Request"
     * 3. Static page by exact path, or dynamic route matching
     * 4. Per-page BEFORE middleware, page content, per-page AFTER middleware
     * 5. Global AFTER middleware
     */
    internal fun route(
        requestDTO: RequestDTO,
        clientHandler: ClientHandler,
    ) {
        val client = clientHandler.client
        // Request lifecycle events removed; Bootstrap manages explicit hooks only
        val rawTarget = requestDTO.target
        val qMark = rawTarget.indexOf('?')
        val target = if (qMark >= 0) rawTarget.take(qMark) else rawTarget
        val query: Map<String, String> = parseQuery(rawTarget)
        var usedPage: Page? = null
        var pageBeforeEmitted = false
        val pre = middlewareProcessBefore(requestDTO.toResult())
        val response =
            if (pre != null) {
                // Global BEFORE short-circuited: still run per-page AFTER for the target page (or 404)
                usedPage = routes[target] ?: RouteCheck.nullPage
                pre
            } else {
                // Give special routes a chance to short-circuit (no per-page hooks when they do)
                val special = Bootstrap.tryHandleSpecialRoute(requestDTO, query, clientHandler)
                if (special != null) {
                    special
                } else {
                    val staticPage = routes[target]
                    if (staticPage != null) {
                        val page = staticPage
                        usedPage = page
                        synchronized(page) {
                            page.queries = query
                            page.request = requestDTO
                            // Per-page event system removed; keep behavior otherwise

                            page.middlewareProcessBefore()
                                ?: handleResponse(page, clientHandler, target)
                        }
                    } else {
                        handleDynamic(requestDTO, query)
                            ?: run {
                                val page = RouteCheck.nullPage
                                usedPage = page
                                synchronized(page) {
                                    page.queries = query
                                    page.request = requestDTO
                                    // No per-page events; just run middleware/content
                                    page.middlewareProcessBefore()
                                        ?: page.content()
                                }
                            }
                    }
                }
            }

        // After deciding the response from BEFORE middleware/handler

        response._request = requestDTO

        if (usedPage != null) {
            val page = usedPage!!
            synchronized(page) {
                page.middlewareProcessAfter(response.toResult())
            }
        }

        middlewareProcessAfter(
            response.toResult(),
        )
        val out = client.getOutputStream()
        out.writeHTTP(
            response,
            clientHandler.server.httpVersion,
        )
    }

    /**
     * Sends an error response using the configured [ExceptionPage] when an exception [e]
     * occurs during request handling for the given [clientHandler].
     */
    internal fun error(
        clientHandler: ClientHandler,
        e: Exception,
    ) {
        val client = clientHandler.client
        // Invoke Bootstrap error handlers; request is unknown at this point
        Bootstrap.fireError(null, e)
        val exPage = RouteCheck.exceptionPage
        synchronized(exPage) {
            exPage.exception = e
            client.getOutputStream().writeHTTP(
                response = exPage.content(),
                version = clientHandler.server.httpVersion,
            )
        }
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
        val page =
            if (routes.containsKey(path)) {
                routes[path] as PageHandler
            } else if (dynamicRoutes.contains(path)) {
                dynamicRoutes[path.split("/")] as PageHandler
            } else {
                val page = PageHandler(path)
                addRoute(page)
                page
            }
        page.builder()
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
 * DSL entry to create a [Router] and configure it with the provided [builder] block.
 */
fun router(builder: Router.() -> Unit): Router {
    val router = Router()
    router.builder()
    return router
}
