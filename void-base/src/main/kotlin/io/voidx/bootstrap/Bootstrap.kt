package io.voidx.bootstrap

import io.voidx.ClientHandler
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.page.Page
import io.voidx.page.PageHandler
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import java.util.*

/**
 * Lightweight bootstrapping service and module lifecycle for Void.
 *
 * External modules can register with [Bootstrap.register] or via Java's [ServiceLoader]
 * by implementing [Bootstrap.Module] and providing the appropriate service descriptor.
 *
 * Phases:
 * - onRouterCreated: Router instance exists; modules can register middleware, routes, HTML hooks, etc.
 * - beforeServerStart: Right before a server socket is started.
 * - afterServerStart: After a server socket has started successfully.
 * - onShutdown: When a server socket stops or the application is shutting down.
 */
object Bootstrap {
    // ---- Hook registries (explicit Bootstrap-managed, no generic events) ----
    private data class DecoratorEntry(
        val id: Long,
        val fn: (Page, Router) -> Unit,
        var active: Boolean = true,
    )

    private val pageDecorators = mutableListOf<DecoratorEntry>()

    @Volatile private var pageDecoratorsSnapshot: List<DecoratorEntry> = emptyList()
    private var nextDecoratorId = 1L

    private val errorHandlers = mutableSetOf<(RequestDTO?, Throwable) -> Unit>()

    @Volatile private var errorHandlersSnapshot: List<(RequestDTO?, Throwable) -> Unit> = emptyList()

    // ---- Special route handlers (pre-dispatch short-circuit) ----
    private data class SpecialRoute(
        val priority: Int,
        val handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?,
    )

    private val specialRoutes = mutableSetOf<SpecialRoute>()

    @Volatile private var specialRoutesSnapshot: List<SpecialRoute> = emptyList()

    // ---- Page decorators ----
    fun registerPageDecorator(decorator: (Page, Router) -> Unit) {
        val entry = DecoratorEntry(nextDecoratorId++, decorator, active = true)
        pageDecorators += entry
        pageDecoratorsSnapshot = pageDecorators.filter { it.active }
    }

    fun unregisterPageDecorator(decorator: (Page, Router) -> Unit) {
        pageDecorators.forEach { if (it.fn === decorator) it.active = false }
        pageDecoratorsSnapshot = pageDecorators.filter { it.active }
    }

    fun addPageDecorator(decorator: (Page, Router) -> Unit): AutoCloseable {
        val entry = DecoratorEntry(nextDecoratorId++, decorator, active = true)
        pageDecorators += entry
        pageDecoratorsSnapshot = pageDecorators.filter { it.active }
        return AutoCloseable {
            entry.active = false
            pageDecoratorsSnapshot = pageDecorators.filter { it.active }
        }
    }

    internal fun runPageDecorators(
        page: Page,
        router: Router,
    ) {
        val snapshot = pageDecoratorsSnapshot
        for (d in snapshot) {
            try {
                d.fn(page, router)
            } catch (_: Throwable) {
            }
        }
    }

    // ---- Error handlers ----
    fun registerErrorHandler(handler: (RequestDTO?, Throwable) -> Unit) {
        errorHandlers += handler
        errorHandlersSnapshot = errorHandlers.toList()
    }

    fun unregisterErrorHandler(handler: (RequestDTO?, Throwable) -> Unit) {
        errorHandlers.remove(handler)
        errorHandlersSnapshot = errorHandlers.toList()
    }

    fun addErrorHandler(handler: (RequestDTO?, Throwable) -> Unit): AutoCloseable {
        registerErrorHandler(handler)
        return AutoCloseable { unregisterErrorHandler(handler) }
    }

    internal fun fireError(
        request: RequestDTO?,
        throwable: Throwable,
    ) {
        val snapshot = errorHandlersSnapshot
        for (h in snapshot) {
            try {
                h(request, throwable)
            } catch (_: Throwable) {
            }
        }
    }

    // ---- Special route API ----

    /** Register a prioritized pre-dispatch special route handler. Higher [priority] runs first. */
    fun registerSpecialRoute(
        priority: Int = 0,
        handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?,
    ) {
        specialRoutes += SpecialRoute(priority, handler)
        // recompute snapshot sorted by priority desc
        specialRoutesSnapshot = specialRoutes.sortedByDescending { it.priority }
    }

    /** Unregister a previously registered [handler]. */
    fun unregisterSpecialRoute(handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?) {
        // remove all entries with the same handler instance
        specialRoutes.removeAll { it.handler === handler }
        specialRoutesSnapshot = specialRoutes.sortedByDescending { it.priority }
    }

    /** Register and get an [AutoCloseable] handle to unregister. */
    fun addSpecialRoute(
        priority: Int = 0,
        handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?,
    ): AutoCloseable {
        registerSpecialRoute(priority, handler)
        return AutoCloseable { unregisterSpecialRoute(handler) }
    }

    /** Internal: give special handlers a chance to return a response before normal routing. */
    internal fun tryHandleSpecialRoute(
        request: RequestDTO,
        query: Map<String, String>,
        clientHandler: ClientHandler,
    ): ResponseDTO? {
        val snapshot = specialRoutesSnapshot
        for (sr in snapshot) {
            try {
                val resp = sr.handler(request, query, clientHandler)
                if (resp != null) return resp
            } catch (_: Throwable) {
                // ignore faulty handlers
            }
        }
        return null
    }

    /** Context object offered to modules to access core services safely. */
    class Context internal constructor(
        /** Underlying router instance. Exposed for advanced uses; prefer helpers below. */
        val router: Router,
    ) {
        /** Add a single page/route to the router. */
        fun addRoute(page: Page) {
            router.addRoute(page)
        }

        /** Add multiple routes to the router. */
        fun addRoutes(pages: List<Page>) {
            router.addRoutes(pages)
        }

        /** DSL helper to register a route via builder. */
        fun route(
            path: String,
            builder: PageHandler.() -> Unit,
        ) {
            router.route(path, builder)
        }

        /** Register a middleware relay (before/after). */
        fun registerMiddleware(relay: Relay) {
            with(router) { +relay }
        }

        /** Convenience to register a BEFORE middleware. */
        fun registerBefore(relay: RelayBefore) {
            registerMiddleware(relay)
        }

        /** Convenience to register an AFTER middleware. */
        fun registerAfter(relay: RelayAfter) {
            registerMiddleware(relay)
        }

        /** List resource paths bundled in classpath under a folder (supports jars). */
        fun listResources(folder: String): List<String> = listResourcePaths(folder)

        /**
         * Serve all classpath resources under the given [folder] at the provided URL [prefix].
         *
         * Example: serveClasspathResources(prefix = "/static", folder = "public") will expose
         * resources like classpath `public/main.css` at `/static/main.css`.
         */
        fun serveClasspathResources(
            prefix: String,
            folder: String,
        ) {
            // Ensure single leading '/'
            val base = if (prefix.startsWith('/')) prefix else "/$prefix"
            val normalizedPrefix = if (base.endsWith('/')) base.dropLast(1) else base
            val resources = listResourcePaths(folder)
            if (resources.isEmpty()) return
            val cl = Thread.currentThread().contextClassLoader
            for (cpPath in resources) {
                val relative = if (cpPath.startsWith("$folder/")) cpPath.removePrefix("$folder/") else cpPath
                val urlPath = "$normalizedPrefix/$relative"
                route(urlPath) {
                    GET { _, _ ->
                        val stream = cl.getResourceAsStream(cpPath)
                        if (stream == null) {
                            buildResponse<String> {
                                status = 404
                                statusText = "Not Found"
                                headers["Content-Type"] = "text/plain"
                                body = "Not Found"
                            }
                        } else {
                            val bytes = stream.use { it.readAllBytes() }
                            val ct = contentTypeFor(urlPath)
                            buildResponse<ByteArray> {
                                status = 200
                                statusText = "OK"
                                headers["Content-Type"] = ct
                                body = bytes
                            }
                        }
                    }
                }
            }
        }

        /** Serve a single classpath resource [resourcePath] at the URL path [urlPath]. */
        fun serveClasspathFile(
            urlPath: String,
            resourcePath: String,
        ) {
            val normalizedUrl = if (urlPath.startsWith('/')) urlPath else "/$urlPath"
            val cl = Thread.currentThread().contextClassLoader
            route(normalizedUrl) {
                GET { _, _ ->
                    val stream = cl.getResourceAsStream(resourcePath)
                    if (stream == null) {
                        buildResponse<String> {
                            status = 404
                            statusText = "Not Found"
                            headers["Content-Type"] = "text/plain"
                            body = "Not Found"
                        }
                    } else {
                        val bytes = stream.use { it.readAllBytes() }
                        val ct = contentTypeFor(normalizedUrl)
                        buildResponse<ByteArray> {
                            status = 200
                            statusText = "OK"
                            headers["Content-Type"] = ct
                            body = bytes
                        }
                    }
                }
            }
        }

        private fun contentTypeFor(path: String): String {
            val ext = path.substringAfterLast('.', "").lowercase()
            return when (ext) {
                "html", "htm" -> "text/html"
                "css" -> "text/css"
                "js" -> "application/javascript"
                "json" -> "application/json"
                "svg" -> "image/svg+xml"
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "gif" -> "image/gif"
                "txt" -> "text/plain"
                else -> "application/octet-stream"
            }
        }

        /** Register a page decorator. */
        fun registerPageDecorator(decorator: (Page, Router) -> Unit) = Bootstrap.registerPageDecorator(decorator)

        /** Register a page decorator and get a handle to unregister. */
        fun addPageDecorator(decorator: (Page, Router) -> Unit): AutoCloseable = Bootstrap.addPageDecorator(decorator)

        /** Register an error handler. */
        fun registerErrorHandler(handler: (RequestDTO?, Throwable) -> Unit) = Bootstrap.registerErrorHandler(handler)

        /** Register an error handler and get a handle to unregister. */
        fun addErrorHandler(handler: (RequestDTO?, Throwable) -> Unit): AutoCloseable = Bootstrap.addErrorHandler(handler)

        /** Register a prioritized special route handler (pre-dispatch). */
        fun registerSpecialRoute(
            priority: Int = 0,
            handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?,
        ) = Bootstrap.registerSpecialRoute(priority, handler)

        /** Register a special route and get a handle to unregister. */
        fun addSpecialRoute(
            priority: Int = 0,
            handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?,
        ): AutoCloseable = Bootstrap.addSpecialRoute(priority, handler)
    }

    /** Lifecycle interface for bootstrap modules. Provide default no-op methods. */
    interface Module {
        fun onRouterCreated(ctx: Context) {}

        fun beforeServerStart(
            serverKind: ServerKind,
            port: Int,
        ) {}

        fun afterServerStart(
            serverKind: ServerKind,
            port: Int,
        ) {}

        fun onShutdown() {}
    }

    enum class ServerKind { HTTP, HTTPS }

    private val modules = mutableSetOf<Module>()
    private var serviceLoaderLoaded = false

    /** Register a module programmatically. Safe to call multiple times; duplicates ignored. */
    fun register(module: Module) {
        modules += module
    }

    /** Load modules using Java ServiceLoader; only performed once. */
    fun loadFromServiceLoader() {
        if (serviceLoaderLoaded) return
        serviceLoaderLoaded = true
        try {
            ServiceLoader.load(Module::class.java, Thread.currentThread().contextClassLoader).forEach { modules += it }
        } catch (_: Throwable) {
            // Ignore discovery failures; modules can still register programmatically.
        }
    }

    /** Internal: notify modules that router is created. */
    internal fun fireRouterCreated(router: Router) {
        loadFromServiceLoader()
        val ctx = Context(router)
        modules.forEach { m ->
            try {
                m.onRouterCreated(ctx)
            } catch (_: Throwable) {
            }
        }
    }

    fun fireBeforeServerStart(
        kind: ServerKind,
        port: Int,
    ) {
        loadFromServiceLoader()
        modules.forEach { m ->
            try {
                m.beforeServerStart(kind, port)
            } catch (_: Throwable) {
            }
        }
    }

    fun fireAfterServerStart(
        kind: ServerKind,
        port: Int,
    ) {
        loadFromServiceLoader()
        modules.forEach { m ->
            try {
                m.afterServerStart(kind, port)
            } catch (_: Throwable) {
            }
        }
    }

    fun fireShutdown() {
        modules.forEach { m ->
            try {
                m.onShutdown()
            } catch (_: Throwable) {
            }
        }
    }
}
