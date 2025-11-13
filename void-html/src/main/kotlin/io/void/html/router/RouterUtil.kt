package io.void.html.router

import io.void.api.CssPage
import io.void.dto.http.buildRequest
import io.void.dto.http.emptyResponse
import io.void.generator.TailwindGen
import io.void.html.Element
import io.void.html.page.JsPage
import io.void.html.page.KtsPage
import io.void.html.page.addCssToRouter
import io.void.html.page.metadata
import io.void.router.HtmlIntegration
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import io.void.router.toResult
import java.util.UUID

private val ktsPages = mutableMapOf<String, KtsPage>()
private val Router.ktsResponsePages
    get() = ktsPages

object RouterUtil {
    init {
        HtmlIntegration.getKtsPage = { target, query, requestDTO, clientHandler ->
            if (ktsResponsePages.containsKey(target)) {
                val page = ktsResponsePages[target] as KtsPage
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
        val paths = listResourcePaths("js")
        paths.forEach { path ->
            val content = readResourceText("/$path", this::class.java)
            HtmlIntegration.jsPages.add(JsPage(UUID.randomUUID(), content))
        }
        HtmlIntegration.handleJsAndCss = { route, router ->
            route.addCssToRouter(router)
            if (route::class != CssPage::class) {
                if (route.metadata != null) {
                    route.request = buildRequest { }
                    if (route.includeTailwind) TailwindGen.processTailwind(route, this)
                    if (route.includeKts) JsPage.addToMetadata(route, HtmlIntegration.jsPages.toList() as List<JsPage>)
                }
            }
        }
    }
}