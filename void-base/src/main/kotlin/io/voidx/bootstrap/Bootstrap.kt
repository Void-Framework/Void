package io.voidx.bootstrap

import io.voidx.ClientHandler
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.page.Page
import io.voidx.page.PageHandler
import io.voidx.middleware.Relay
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import io.voidx.util.ModuleInit
import java.util.ServiceLoader

// ---- HTML/Resources integration types (formerly HtmlIntegration) ----
typealias GetKtsPageFn = Router.(String, Map<String, String>, RequestDTO, ClientHandler) -> ResponseDTO
typealias HandleJsAndCss = (Page, Router) -> Unit

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
    // ---- HTML/Resources integration replaced here (was HtmlIntegration) ----

    private var ktsHandler: GetKtsPageFn? = null
    private val pageHandlers = mutableListOf<HandleJsAndCss>()

    /** Register the KTS page handler. Can only be registered once. */
    fun registerKtsHandler(fn: GetKtsPageFn) {
        check(ktsHandler == null) { "KTS handler already registered" }
        ktsHandler = fn
    }

    /** Register a page decoration handler. Multiple handlers are allowed. */
    fun registerJsAndCss(fn: HandleJsAndCss) {
        pageHandlers += fn
    }

    internal fun firePageAdded(page: Page, router: Router) {
        pageHandlers.forEach { h ->
            try { h(page, router) } catch (_: Throwable) {}
        }
    }

    internal fun handleKtsIfPresent(router: Router, target: String, query: Map<String, String>, requestDTO: RequestDTO, clientHandler: ClientHandler): ResponseDTO? {
        val handler = ktsHandler ?: return null
        return try { handler.invoke(router, target, query, requestDTO, clientHandler) } catch (_: Throwable) { null }
    }
    /** Context object offered to modules to access core services safely. */
    class Context internal constructor(
        /** Underlying router instance. Exposed for advanced uses; prefer helpers below. */
        val router: Router,
    ) {
        /** Register KTS handler used by Router when KTS-Request header is present. */
        fun registerKtsHandler(fn: GetKtsPageFn) = Bootstrap.registerKtsHandler(fn)

        /** Register a page decoration hook to attach JS/CSS/resources. */
        fun registerJsAndCss(fn: HandleJsAndCss) = Bootstrap.registerJsAndCss(fn)

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
                ModuleInit.initializers.forEach { it.init() }
            } catch (_: Throwable) {
            }
        }
        val ctx = Context(router)
        modules.forEach { m ->
            try { m.onRouterCreated(ctx) } catch (_: Throwable) {}
        }
    }

    fun fireBeforeServerStart(kind: ServerKind, port: Int) {
        loadFromServiceLoader()
        modules.forEach { m ->
            try { m.beforeServerStart(kind, port) } catch (_: Throwable) {}
        }
    }

    fun fireAfterServerStart(kind: ServerKind, port: Int) {
        loadFromServiceLoader()
        modules.forEach { m ->
            try { m.afterServerStart(kind, port) } catch (_: Throwable) {}
        }
    }

    fun fireShutdown() {
        modules.forEach { m ->
            try { m.onShutdown() } catch (_: Throwable) {}
        }
    }
}
