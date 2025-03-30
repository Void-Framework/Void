package io.void.router.util

import io.void.cache.Cache
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.http.builder.HTTPBuilder
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

internal interface RequestHandler {

    val builder: HTTPBuilder
    val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage<*>>

    fun handleDynamic(requestDTO: RequestDTO): ResponseDTO? {
        val target = requestDTO.target
        val url = target.split('/')
        if (url.contains("favicon.ico")) return null
        dynamicRoutes.forEach { (target, route) ->
            var matches = true
            target.forEachIndexed { i, pTarget ->
                try {
                    if (url[i] != pTarget) {
                        if (pTarget != "{}") {
                            matches = false
                            return@forEachIndexed
                        }
                    }
                } catch (_: Exception) {
                    return@forEachIndexed
                }
            }

            if (matches) {
                route.request = requestDTO
                return route.content().let { content ->
                    when (content) {
                        is ContentType.Response -> content.response
                        is ContentType.HtmlElements -> constructClassicResponse(page = route)
                    }
                }
            }
        }
        return null
    }

    fun<T : Page<*>> constructClassicResponse(page: T): ResponseDTO {
        return ResponseDTO(
            status = 200,
            statusText = "All is well",
            headers = mutableMapOf("Content-Type" to "text/html"),
            body = "<!doctype html><html><head>${page.metadata?.render()}</head><body>${(page.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
        )
    }

    fun handleResponse(page: Page<ContentType.Response>, client: Socket) {
        builder.build(
            response = page.content().response,
            outputStream = client.getOutputStream()
        )
    }

    fun handleCasual(page: Page<ContentType.HtmlElements>, client: Socket, target: String) {
        val response = if (Cache.singleton.cache.containsKey(target)) {
            Cache.singleton.cache[target]!!
        } else {
            constructClassicResponse(
                page = page
            )
        }

        builder.build(
            response = response,
            outputStream = client.getOutputStream()
        )
    }
}