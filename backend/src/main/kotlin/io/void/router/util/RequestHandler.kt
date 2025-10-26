package io.void.router.util

import io.void.api.KtsPage
import io.void.cache.Cache
import io.void.clienthandler.ClientHandler
import io.void.dto.http.*
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.dynamic.Path
import io.void.router.toResult
import java.util.concurrent.ConcurrentHashMap

/**
 * Internal helpers for routing requests to the correct handler and building
 * responses for different page types (HTML, API, KTS, dynamic).
 */
internal interface RequestHandler {
    val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage<*>>

    fun handleDynamic(
        requestDTO: RequestDTO,
        query: Map<String, String>,
    ): ResponseDTO? {
        val segmentRegex = Regex("""^\{([^{}]+)}$""")
        val optionalSegment = Regex("""^\{([^{}?]+)\?}$""")
        val requestTarget = requestDTO.target
        val url = requestTarget.split('/').toMutableList()
        if (url.contains("favicon.ico")) return null
        dynamicRoutes.forEach { (target, route) ->
            val dynamics = mutableMapOf<Path, String>()
            val mutableTarget = target.toMutableList()
            url.trimTrailingEmpty()
            mutableTarget.trimTrailingEmpty()
            if (url.size != mutableTarget.size) {
                if (mutableTarget.last().matches(optionalSegment) && url.size + 1 == mutableTarget.size) {
                    mutableTarget.removeLast()
                } else {
                    return@forEach
                }
            }
            url.forEachIndexed { i, segment ->
                val targetValue = mutableTarget[i]
                if (segment == targetValue) {
                    return@forEachIndexed
                } else if (targetValue.matches(segmentRegex)) {
                    val match = segmentRegex.matchEntire(targetValue)!!.groupValues[1]
                    dynamics[match] = url[i]
                } else {
                    return@forEach
                }
            }

            route._data = dynamics
            route.request = requestDTO
            route.queries = query

            val response = route.middlewareProcessBefore(requestDTO.toResult())

            return response ?: when (route.contentType) {
                ContentType.HtmlElements::class -> constructClassicResponse(route)
                else -> (route.content() as ContentType.Response).response
            }
        }
        return null
    }

    fun <T : Page<*>> constructClassicResponse(page: T): ResponseDTO =
        buildResponse {
            status = 200
            statusText = "All is well"
            headers {
                put("Content-Type", "text/html")
            }
            body =
                """
                <!doctype html><html>
                <head>${page.metadata?.render() ?: ""}</head>
                <body>${(page.content() as ContentType.HtmlElements).htmlElement.render()}</body>
                </html>
                """.trimIndent()
        }

    fun handleResponse(
        page: Page<ContentType.Response>,
        clientHandler: ClientHandler,
    ): ResponseDTO = page.content().response

    fun handleCasual(
        page: Page<ContentType.HtmlElements>,
        clientHandler: ClientHandler,
        target: String,
    ): ResponseDTO {
        val client = clientHandler.client
        return if (Cache.cache.containsKey(target)) {
            Cache[target]!!
        } else {
            constructClassicResponse(
                page = page,
            )
        }
    }

    fun handleKts(
        page: KtsPage,
        clientHandler: ClientHandler,
    ): ResponseDTO {
        val client = clientHandler.client
        val response =
            if (Cache.cache.containsKey(page.target)) {
                Cache[page.target]!!
            } else {
                buildResponse {
                    status = 200
                    statusText = "All is well"
                    headers {
                        put("Content-Type", "text/html")
                    }
                    body =
                        (page.content() as ContentType.HtmlElements).htmlElement.render().trimIndent()
                }
            }
        return response
    }
}

fun MutableList<String>.trimTrailingEmpty(): Boolean {
    val hasEmptyTail = this.lastOrNull()?.isEmpty() == true
    if (hasEmptyTail) removeAt(lastIndex)
    return hasEmptyTail
}
