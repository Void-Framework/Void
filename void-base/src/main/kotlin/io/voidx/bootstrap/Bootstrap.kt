package io.voidx.bootstrap

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.ClientHandler
import io.voidx.page.Page
import io.voidx.page.PageHandler
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import io.voidx.util.ModuleInit
import java.util.ServiceLoader

// Event system to allow external modules to hook into internals without hard deps
sealed interface Event {
    data class RouterCreated(val router: Router) : Event
    data class PageAdded(val page: Page, val router: Router) : Event
    // Per-page lifecycle events (static and fallback pages)
    data class PageBefore(val request: RequestDTO, val page: Page) : Event
    data class PageAfter(val response: ResponseDTO, val page: Page) : Event
    data class ServerStarting(val kind: Bootstrap.ServerKind, val port: Int) : Event
    data class ServerStarted(val kind: Bootstrap.ServerKind, val port: Int) : Event
    // Request lifecycle (observability only)
    data class RequestStart(val request: RequestDTO) : Event
    data class BeforeGlobalMiddleware(val request: RequestDTO) : Event
    data class AfterGlobalMiddleware(val response: ResponseDTO) : Event
    data class RequestEnd(val request: RequestDTO, val response: ResponseDTO) : Event
    data class Error(val request: RequestDTO?, val throwable: Throwable) : Event
    data object ServerShutdown : Event
}

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
 *
 * Backward compatibility: existing [ModuleInit] initializers are invoked through a bridge
 * during the first [onRouterCreated] call to preserve current behavior.
 */
object Bootstrap {
    // ---- Event listeners ----
    private val listeners = mutableSetOf<(Event) -> Unit>()
    
    // ---- Special route handlers (pre-dispatch short-circuit) ----
    private data class SpecialRoute(val priority: Int, val handler: (RequestDTO, Map<String, String>, ClientHandler) -> ResponseDTO?)
    private val specialRoutes = mutableSetOf<SpecialRoute>()
    @Volatile private var specialRoutesSnapshot: List<SpecialRoute> = emptyList()

    fun registerListener(listener: (Event) -> Unit) {
        listeners += listener
    }

    /**
     * Registers [listener] and returns an [AutoCloseable] handle that can be used to unregister it.
     * Existing overload [registerListener] remains for backward compatibility.
     */
    fun addListener(listener: (Event) -> Unit): AutoCloseable {
        registerListener(listener)
        return AutoCloseable { unregisterListener(listener) }
    }

    fun unregisterListener(listener: (Event) -> Unit) {
        listeners -= listener
    }

    internal fun emit(event: Event) {
        // snapshot to avoid concurrent modification
        val snapshot = listeners.toList()
        for (l in snapshot) {
            try { l(event) } catch (_: Throwable) {}
        }
    }

    /** Notify listeners that a page has been added to a router. */
    fun firePageAdded(page: Page, router: Router) {
        emit(Event.PageAdded(page, router))
    }

    // ---- Request lifecycle event helpers ----
    fun fireRequestStart(request: RequestDTO) {
        emit(Event.RequestStart(request))
    }

    fun fireBeforeGlobalMiddleware(request: RequestDTO) {
        emit(Event.BeforeGlobalMiddleware(request))
    }

    fun fireAfterGlobalMiddleware(response: ResponseDTO) {
        emit(Event.AfterGlobalMiddleware(response))
    }

    fun fireRequestEnd(request: RequestDTO, response: ResponseDTO) {
        emit(Event.RequestEnd(request, response))
    }

    fun fireError(request: RequestDTO?, throwable: Throwable) {
        emit(Event.Error(request, throwable))
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
        fun addRoute(page: Page) { router.addRoute(page) }

        /** Add multiple routes to the router. */
        fun addRoutes(pages: List<Page>) { router.addRoutes(pages) }

        /** DSL helper to register a route via builder. */
        fun route(path: String, builder: PageHandler.() -> Unit) { router.route(path, builder) }

        /** Register a middleware relay (before/after). */
        fun registerMiddleware(relay: Relay) { with(router) { +relay } }

        /** Convenience to register a BEFORE middleware. */
        fun registerBefore(relay: RelayBefore) { registerMiddleware(relay) }

        /** Convenience to register an AFTER middleware. */
        fun registerAfter(relay: RelayAfter) { registerMiddleware(relay) }

        /** List resource paths bundled in classpath under a folder (supports jars). */
        fun listResources(folder: String): List<String> = listResourcePaths(folder)

        /** Subscribe to bootstrap events. */
        fun onEvent(listener: (Event) -> Unit) = registerListener(listener)

        /** Subscribe and get a handle you can close to unsubscribe. */
        fun onEventHandle(listener: (Event) -> Unit): AutoCloseable = addListener(listener)

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
        fun beforeServerStart(serverKind: ServerKind, port: Int) {}
        fun afterServerStart(serverKind: ServerKind, port: Int) {}
        fun onShutdown() {}
    }

    enum class ServerKind { HTTP, HTTPS }

    private val modules = mutableSetOf<Module>()
    private var serviceLoaderLoaded = false
    private var legacyInitRun = false

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

    /** Internal: fire router created event. */
    fun fireRouterCreated(router: Router) {
        loadFromServiceLoader()
        // Bridge: run legacy ModuleInit initializers once when router is first created.
        if (!legacyInitRun) {
            legacyInitRun = true
            try {
                ModuleInit.runAllInitializers()
            } catch (_: Throwable) {
            }
        }
        val ctx = Context(router)
        modules.forEach { m ->
            try { m.onRouterCreated(ctx) } catch (_: Throwable) {}
        }
        emit(Event.RouterCreated(router))
    }

    fun fireBeforeServerStart(kind: ServerKind, port: Int) {
        loadFromServiceLoader()
        modules.forEach { m ->
            try { m.beforeServerStart(kind, port) } catch (_: Throwable) {}
        }
        emit(Event.ServerStarting(kind, port))
    }

    fun fireAfterServerStart(kind: ServerKind, port: Int) {
        loadFromServiceLoader()
        modules.forEach { m ->
            try { m.afterServerStart(kind, port) } catch (_: Throwable) {}
        }
        emit(Event.ServerStarted(kind, port))
    }

    fun fireShutdown() {
        modules.forEach { m ->
            try { m.onShutdown() } catch (_: Throwable) {}
        }
        emit(Event.ServerShutdown)
    }
}
