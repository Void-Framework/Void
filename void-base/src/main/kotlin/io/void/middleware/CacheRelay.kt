package io.void.middleware

import io.void.cache.Cache
import io.void.cache.RecomputeFlag
import io.void.dto.http.buildRequest
import io.void.html.page.Page

/**
 * Registers the current [io.void.html.page.Page] for caching with a refresh [duration] in milliseconds.
 *
 * The [recompute] flag controls whether the cache will be periodically recomputed. When set to `false`,
 * the background refresh loop stops and the cached entry is removed.
 */
context(page: Page)
fun cache(
    duration: Int,
    recompute: RecomputeFlag = RecomputeFlag(true),
) {
    page.request = buildRequest { }
    Cache.cacheRoute(mapOf(page to duration), recompute)
}
