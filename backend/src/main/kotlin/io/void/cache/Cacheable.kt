package io.void.cache

import io.void.html.page.Page
import kotlin.reflect.full.findAnnotation

/**
 * Annotation to mark a [Page] as cacheable with a given invalidation period in milliseconds.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cacheable(
    val invalidationDurationInMillies: Int,
)

/** Processes pages annotated with [Cacheable] and registers them with [Cache]. */
internal object CacheProcessor {
    fun processCacheables(page: Page<*>) {
        val cacheRoutes = mutableMapOf<Page<*>, Int>()
        val cacheable = page::class.findAnnotation<Cacheable>()
        if (cacheable != null) {
            cacheRoutes[page] = cacheable.invalidationDurationInMillies
        }

        Cache.cacheRoute(cacheRoutes)
    }
}
