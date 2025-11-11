package io.void.router.util

import io.void.api.KtsPage
import io.void.cache.Cache
import io.void.clienthandler.ClientHandler
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.dynamic.Path
import io.void.router.toResult
import java.util.concurrent.ConcurrentHashMap

/**
 * Internal helpers for routing requests to the correct handler and building
 * responses for different page types (HTML, API, KTS, dynamic).
 */
internal interface RequestHandler {
    val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage>

    companion object {
        private val segmentRegex = Regex("""^\{([^{}]+)}$""")
        private val optionalSegment = Regex("""^\{([^{}?]+)\?}$""")
    }

    fun handleDynamic(
        requestDTO: RequestDTO,
        query: Map<String, String>,
    ): ResponseDTO? {
        val requestTarget = requestDTO.target
        if (requestTarget.endsWith("favicon.ico")) return null
        val url = requestTarget.split('/').toMutableList()
        dynamicRoutes.forEach { (target, route) ->
            val dynamics = mutableMapOf<Path, String>()
            val mutableTarget = target.toMutableList()
            url.trimTrailingEmpty()
            mutableTarget.trimTrailingEmpty()
            if (url.size != mutableTarget.size) {
                if (mutableTarget.isNotEmpty() &&
                    mutableTarget
                        .last()
                        .matches(optionalSegment) && url.size + 1 == mutableTarget.size
                ) {
                    mutableTarget.removeLast()
                } else {
                    return@forEach
                }
            }
            url.forEachIndexed { i, segment ->
                val targetValue = mutableTarget[i]
                if (segment == targetValue) {
                    return@forEachIndexed
                } else if (segmentRegex.matches(targetValue)) {
                    val match = segmentRegex.matchEntire(targetValue)!!.groupValues[1]
                    dynamics[match] = url[i]
                } else {
                    return@forEach
                }
            }

            route._data = dynamics
            route.request = requestDTO
            route.queries = query

            val response = route.middlewareProcessBefore(requestDTO.toResult())

            return response ?: route.content()
        }
        return null
    }

    fun handleResponse(
        page: Page,
        clientHandler: ClientHandler,
        target: String
    ): ResponseDTO = if (Cache.cache.containsKey(target)) {
        Cache[target]!!
    } else {
        page.content()
    }
}

/**
 * Removes a single trailing empty string element from this list if present.
 * Useful when splitting URL paths to ignore a trailing slash.
 *
 * @return true if an empty element was removed; false otherwise.
 */
fun MutableList<String>.trimTrailingEmpty(): Boolean {
    val hasEmptyTail = this.lastOrNull()?.isEmpty() == true
    if (hasEmptyTail) removeAt(lastIndex)
    return hasEmptyTail
}
