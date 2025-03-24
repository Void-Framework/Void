package io.void.cache

import io.void.cache.exception.CacheException
import io.void.dto.ResponseDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

internal class Cache private constructor() {

    companion object {
        val singleton = Cache()
    }

    val cache: ConcurrentHashMap<String, ResponseDTO> = ConcurrentHashMap()
    private val pool = Executors.newCachedThreadPool()

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
            cache[page.target] = ResponseDTO(
                status = 200,
                statusText = "All is well",
                headers = mutableMapOf(
                    "Content-Type" to "text/html"
                ),
                body = "<html><body>${(page.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
            )
        } else {
            cache[page.target] = (page.content() as ContentType.Response).response
        }
    }

    private fun handleCache(route: Pair<Page<*>, Int>) {
        val (page, duration) = route
        if (duration <= 0) return
        pool.execute {
            try {
                while (!Thread.currentThread().isInterrupted) {
                    Thread.sleep(duration.toLong())
                    putInCache(route)
                }
            } catch (e: Exception) {
                Thread.currentThread().interrupt()
                throw CacheException(e)
            }
        }
    }
}