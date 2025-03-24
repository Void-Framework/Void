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

        fun annotationProcessor(pages: List<Page<*>>, router: Router) {
            val cacheRoutes = mutableMapOf<Page<*>, Int>()
            pages.forEach { page ->
                val cacheable = page::class.findAnnotation<Cacheable>()
                if (cacheable != null) {
                    //val function = pages::class.functions.find { it.visibility != null && it.visibility!! == KVisibility.PUBLIC &&  it.findAnnotation<Cacheable>() != null && it.name == "content" }
                    //val value = pages::class.declaredMemberProperties.find { it.visibility != null && it.visibility!! == KVisibility.PUBLIC && it.findAnnotation<Cacheable>() != null }

                    cacheRoutes[page] = cacheable.invalidationDurationInMillies
                }
            }
            Cache.singleton.cacheRoute(cacheRoutes)
        }
    }
}


