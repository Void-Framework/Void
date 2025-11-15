package io.void.router

import io.void.clienthandler.ClientHandler
import io.void.dto.http.*
import io.void.html.page.ExceptionPage
import io.void.html.page.NotFoundPage
import io.void.html.page.Page
import io.void.html.page.dynamic.DynamicPage
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.page.PageHandler
import io.void.router.util.RequestHandler
import io.void.router.util.RouteCheck
import io.void.util.HtmlIntegration
import java.io.File
import java.net.URLDecoder
import java.util.*
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

    init {
        recomputeMiddlewareSnapshot()
        HtmlIntegration.jsPages.forEach { addRoute(it) }
    }

    /** Adds this page to the router using unary plus syntax: +page. */
    operator fun Page.unaryPlus() {
        addRoute(this)
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

    internal fun middlewareProcessAfter(response: Result<ResponseDTO>) {
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
        HtmlIntegration.handleJsAndCss?.let { it(route, this) }
        when (route) {
            is ExceptionPage -> {
                RouteCheck.exceptionPage = route
            }

            is NotFoundPage -> {
                RouteCheck.nullPage = route
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
    internal fun addRoutes(routes: List<Page>): Router {
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
    fun route(
        requestDTO: RequestDTO,
        clientHandler: ClientHandler,
    ) {
        val client = clientHandler.client
        val rawTarget = requestDTO.target
        val qMark = rawTarget.indexOf('?')
        val target = if (qMark >= 0) rawTarget.take(qMark) else rawTarget
        val query: Map<String, String> =
            if (qMark >= 0 && qMark + 1 < rawTarget.length) {
                val map = LinkedHashMap<String, String>(4)
                val qs = rawTarget.substring(qMark + 1)
                var start = 0
                while (start < qs.length) {
                    val amp = qs.indexOf('&', start).let { if (it == -1) qs.length else it }
                    if (amp > start) {
                        val eq = qs.indexOf('=', start).let { if (it == -1 || it > amp) -1 else it }
                        if (eq != -1) {
                            val key = qs.substring(start, eq)
                            val value = qs.substring(eq + 1, amp)
                            map[key] = value
                        }
                    }
                    start = amp + 1
                }
                map
            } else {
                emptyMap()
            }
        val response =
            middlewareProcessBefore(requestDTO.toResult()) ?: when {
                requestDTO.headers.containsKey("KTS-Request") -> {
                    HtmlIntegration.getKtsPage?.let { it(this, target, query, requestDTO, clientHandler) }
                        ?: emptyResponse()
                }

                else -> {
                    val staticPage = routes[target]
                    if (staticPage != null) {
                        val page = staticPage
                        synchronized(page) {
                            page.queries = query
                            page.request = requestDTO

                            page.middlewareProcessBefore(requestDTO.toResult())
                                ?: handleResponse(page, clientHandler, target)
                        }
                    } else {
                        handleDynamic(requestDTO, query)
                            ?: run {
                                val page = RouteCheck.nullPage
                                synchronized(page) {
                                    page.queries = query
                                    page.request = requestDTO
                                    page.middlewareProcessBefore(requestDTO.toResult())
                                        ?: page.content()
                                }
                            }
                    }
                }
            }

        response._request = requestDTO

        val page = routes[target] ?: RouteCheck.nullPage
        synchronized(page) {
            page.middlewareProcessAfter(response.toResult())
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
    fun error(
        clientHandler: ClientHandler,
        e: Exception,
    ) {
        val client = clientHandler.client
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
     * Returns a [PageHandler] for the given static [path], creating and registering one if missing.
     * Allows a fluent style to register per-method handlers (e.g., on("/api") GET { ... }).
     */

    /**
     * Returns a [PageHandler] for the static [path], creating and registering one
     * if it does not exist yet. Allows fluent per-method handlers, e.g.:
     * router.on("/api").GET { ... }
     */
    fun on(path: String): PageHandler =
        if (routes.containsKey(path)) {
            routes[path] as PageHandler
        } else {
            val page = PageHandler(path)
            addRoute(page)
            page
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
    val router = Router().apply(builder)
    return router
}

/** Wraps this value in a successful [Result]. */
fun <T> T.toResult(): Result<T> = Result.success(this)

/** Wraps this exception in a failed [Result]. */
fun <T> Exception.toResult(): Result<T> = Result.failure(this)

/**
 * Reads the classpath resource at [path] using the class loader of [clazz].
 * Useful when loading resources packaged alongside a specific class.
 */
fun readResourceText(
    path: String,
    clazz: Class<*>,
): String =
    clazz
        .getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")

/**
 * Reads the classpath resource at [path] using the thread context class loader.
 */
fun readResourceText(path: String): String =
    Thread
        .currentThread()
        .contextClassLoader
        .getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")
