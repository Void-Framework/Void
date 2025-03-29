package io.void.cache

import io.void.html.page.Page
import io.void.router.Router
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.functions

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cacheable(val invalidationDurationInMillies: Int)

internal abstract class Processor {

    companion object {

        fun annotationProcessor(page: Page<*>) {
            val cacheRoutes = mutableMapOf<Page<*>, Int>()
            val cacheable = page::class.findAnnotation<Cacheable>()
            if (cacheable != null) {
                cacheRoutes[page] = cacheable.invalidationDurationInMillies
            }

            Cache.singleton.cacheRoute(cacheRoutes)
        }
    }
}


