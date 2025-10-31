package io.void.cache

import io.void.cache.exception.CacheException
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Simple in-memory page response cache with optional timed invalidation.
 *
 * Pages annotated with [io.void.cache.Cacheable] are registered via [CacheProcessor]
 * with their invalidation duration in milliseconds.
 */
internal object Cache {
    internal val cache: ConcurrentHashMap<String, ResponseDTO> = ConcurrentHashMap()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /** Populates/refreshes cache entries for the given [routes] and schedules invalidation. */
    internal fun cacheRoute(routes: Map<Page<*>, Int>, recompute: Boolean) {
        routes.forEach { (route, duration) ->
            try {
                putInCache(route to duration, recompute)
            } catch (e: Exception) {
                throw CacheException(e)
            }
        }
    }

    /** Renders page output and stores it in the cache, then sets up refresh if needed. */
    private fun putInCache(route: Pair<Page<*>, Int>, recompute: Boolean) {
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
        handleCache(route, recompute)
    }

    /** Schedules periodic cache refresh for the given [route] if [duration] > 0. */
    private fun handleCache(route: Pair<Page<*>, Int>, recompute: Boolean) {
        val (page, duration) = route
        if (duration <= 0) return
        scope.launch {
            while (isActive) {
                delay(duration.toLong())
                try {
                    if (recompute) {
                        putInCache(route, true)
                    } else {
                        cache.remove(page.target)
                    }
                } catch (e: Exception) {
                    throw CacheException(e)
                }
            }
        }
    }

    /** Returns the cached [ResponseDTO] for a page target path, if present. */
    operator fun get(route: String): ResponseDTO? = cache[route]
}
