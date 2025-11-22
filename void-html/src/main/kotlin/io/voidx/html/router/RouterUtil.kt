package io.voidx.html.router

import io.voidx.Method
import io.voidx.css.CssPage
import io.voidx.css.TailwindGen
import io.voidx.dto.buildRequest
import io.voidx.dto.emptyResponse
import io.voidx.html.Element
import io.voidx.html.page.JsPage
import io.voidx.html.page.KtsPage
import io.voidx.html.page.addCssToRouter
import io.voidx.html.page.metadata
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import io.voidx.util.HtmlIntegration
import io.voidx.util.ModuleInit
import io.voidx.util.readResourceText
import io.voidx.util.toResult
import java.util.*

/**
 * Wires the HTML module into the core runtime by registering integration hooks.
 *
 * Responsibilities:
 * - Expose a handler for KTS requests via [HtmlIntegration.getKtsPage].
 * - Discover and register embedded JS resources to [HtmlIntegration.jsPages].
 * - Provide a per-page hook [HtmlIntegration.handleJsAndCss] to attach CSS (Tailwind and external)
 *   and JS to newly added routes.
 */
object RouterUtil : ModuleInit() {
    @Volatile
    private var initialized: Boolean = false

    /** Called by the base module at startup to install integration hooks. */
    override fun init() {
        if (initialized) return
        HtmlIntegration.registerKtsPage { target, query, requestDTO, clientHandler ->
            if (routes.containsKey(target)) {
                val page = routes[target] as KtsPage
                page.queries = query
                val route = requestDTO.headers["KTS-Route"]!!
                val content = routes[route]!!.content()
                val rootElement = content.attributes["Element"] as Element
                val triggerId = requestDTO["KTS-Trigger"]
                val targetId = requestDTO["KTS-Target"]
                val trigger = triggerId?.let { rootElement.findElement(it) }
                val targetEl = targetId?.let { rootElement.findElement(it) }
                page._target = targetEl
                page._trigger = trigger
                page.request = requestDTO

                page.middlewareProcessBefore(requestDTO.toResult())
                    ?: handleResponse(page, clientHandler, target)
            } else {
                emptyResponse()
            }
        }
        HtmlIntegration.registerJsAndCss { route, router ->
            route.addCssToRouter(router)
            if (route::class != CssPage::class) {
                route.request = buildRequest { method = Method.GET }
                if (route.metadata != null) {
                    if (route.includeTailwind) TailwindGen.processTailwind(route, router)
                    if (route.includeKts) JsPage.addToMetadata(route, HtmlIntegration.jsPages.toList() as List<JsPage>)
                }
            }
        }
        val paths = listResourcePaths("js")
        paths.forEach { path ->
            val content = readResourceText("/$path", this::class.java)
            val jsPage = JsPage(UUID.randomUUID(), content)
            HtmlIntegration.addJsPage(jsPage)
            Router.routers.forEach { it.addRoute(jsPage) }
        }
        initialized = true
    }

    // Ensure that merely referencing RouterUtil (object initialization) installs hooks in test environments
    init {
        if (!initialized) {
            try {
                init()
            } catch (_: Throwable) {
                // Swallow to avoid failing static init in environments lacking resources; tests can still proceed
            }
        }
    }
}
