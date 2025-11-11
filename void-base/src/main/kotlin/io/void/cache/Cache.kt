package io.void.cache

import io.void.cache.exception.CacheException
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory cache for rendered page responses with optional periodic recomputation.
 *
 * Usage:
 * - Pages are registered with a duration (milliseconds) controlling refresh cadence.
 * - For HTML pages ([ContentType.HtmlElements]), the page is rendered to a complete HTML document.
 * - For API pages ([ContentType.Response]), the produced [ResponseDTO] is cached directly.
 * - If duration <= 0, the response is cached once and not refreshed.
 * - Refreshing continues while [RecomputeFlag.value] is true; when it becomes false, the entry is removed.
 */
internal object Cache {
    /** Internal store keyed by page target path. */
    internal val cache: ConcurrentHashMap<String, ResponseDTO> = ConcurrentHashMap()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /** Populates/refreshes cache entries for the given [routes] and schedules recomputation if applicable. */
    internal fun cacheRoute(
        routes: Map<Page, Int>,
        recompute: RecomputeFlag,
    ) {
        routes.forEach { (route, duration) ->
            try {
                putInCache(route to duration, recompute)
            } catch (e: Exception) {
                throw CacheException(e)
            }
        }
    }

    /** Renders page output and stores it in the cache, then sets up refresh if needed. */
    private fun putInCache(
        route: Pair<Page, Int>,
        recompute: RecomputeFlag,
    ) {
        val (page, duration) = route
        cache[page.target] = page.content()
        handleCache(route, recompute)
    }

    /** Schedules periodic cache refresh for the given [route] if [duration] > 0. */
    private fun handleCache(
        route: Pair<Page, Int>,
        recompute: RecomputeFlag,
    ) {
        val (page, duration) = route
        if (duration <= 0) return
        scope.launch {
            while (recompute.value) {
                delay(duration.toLong())
                try {
                    putInCache(route, recompute)
                } catch (e: Exception) {
                    throw CacheException(e)
                }
            }
            cache.remove(page.target)
        }
    }

    /** Returns the cached [ResponseDTO] for a page target path, if present. */
    operator fun get(route: String): ResponseDTO? = cache[route]
}

/**
 * Mutable flag controlling whether cached entries keep being recomputed.
 * Set [value] to false to stop the refresh loop and evict the entry on the next check.
 */
data class RecomputeFlag(
    @Volatile var value: Boolean,
)
