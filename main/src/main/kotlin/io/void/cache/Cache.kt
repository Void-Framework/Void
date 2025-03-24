package io.void.cache

import io.void.dto.ResponseDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.router.Router
import java.util.concurrent.ConcurrentHashMap

internal class Cache private constructor() {

    companion object {
        val singleton = Cache()
    }

    val cache: ConcurrentHashMap<String, ResponseDTO> = ConcurrentHashMap()

    internal fun cacheRoute(routes: Map<Page<*>, Int>) {
        routes.forEach { (route, duration) ->
            if (route.contentType != ContentType.Response::class) {
                try {
                    cache[route.target] = ResponseDTO(
                        status = 200,
                        statusText = "All is well",
                        headers = mutableMapOf(
                            "Content-Type" to "text/html",
                        ),
                        body = "<html><body>${(route.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                cache[route.target] = (route.content() as ContentType.Response).response
            }
        }
    }
}