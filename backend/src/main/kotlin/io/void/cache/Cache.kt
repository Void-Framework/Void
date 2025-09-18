package io.void.cache

import io.void.cache.exception.CacheException
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

internal object Cache {
    val cache: ConcurrentHashMap<String, ResponseDTO> = ConcurrentHashMap()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    internal fun cacheRoute(routes: Map<Page<*>, Int>) {
        routes.forEach { (route, duration) ->
            try {
                putInCache(route to duration)
            } catch (e: Exception) {
                throw CacheException(e)
            }
        }
    }

    private fun putInCache(route: Pair<Page<*>, Int>) {
        val (page, duration) = route
        if (page.contentType != ContentType.Response::class) {
            val metadata = page.metadata
            cache[page.target] =
                buildResponse {
                    status = 200
                    statusText = "All is well"
                    headers {
                        put("Content-Type", "text/html")
                    }
                    body =
                        """
                        <!doctype html><html>
                        <head>${metadata?.render() ?: ""}</head>
                        <body>${(page.content() as ContentType.HtmlElements).htmlElement.render()}</body>
                        </html>
                        """.trimIndent()
                }
        } else {
            cache[page.target] = (page.content() as ContentType.Response).response
        }
        handleCache(route)
    }

    private fun handleCache(route: Pair<Page<*>, Int>) {
        val (page, duration) = route
        if (duration <= 0) return
        scope.launch {
            while (isActive) {
                delay(duration.toLong())
                try {
                    putInCache(route)
                } catch (e: Exception) {
                    throw CacheException(e)
                }
            }
        }
    }
}
