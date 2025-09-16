package io.void.cache

import io.void.html.page.Page
import kotlin.reflect.full.findAnnotation

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cacheable(val invalidationDurationInMillies: Int)

internal object Processor {

    fun annotationProcessor(page: Page<*>) {
        val cacheRoutes = mutableMapOf<Page<*>, Int>()
        val cacheable = page::class.findAnnotation<Cacheable>()
        if (cacheable != null) {
            cacheRoutes[page] = cacheable.invalidationDurationInMillies
        }

        Cache.cacheRoute(cacheRoutes)
    }
}


