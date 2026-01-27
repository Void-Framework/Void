package io.voidx.router.util

import io.voidx.ClientHandler
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.page.DynamicPage
import io.voidx.page.Page
import io.voidx.page.Path
import io.voidx.util.toResult
import io.voidx.util.trimTrailingEmpty
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

    /**
     * Attempts to resolve the incoming [requestDTO] against registered dynamic routes.
     *
     * Matching rules:
     * - Route patterns are tokenized by '/'. Static segments must match exactly.
     * - Dynamic segments use "{name}" syntax and accept any single path token; the
     *   captured values are exposed to the page in [DynamicPage._data].
     * - Optional trailing segments use "{name?}" and may be omitted by the request.
     * - A single trailing slash at the end of either the request or pattern is ignored.
     *
     * If a route matches, the page's request context and [query] map are populated, the
     * page BEFORE middleware is executed, and finally [DynamicPage.content] is produced.
     *
     * @return a [ResponseDTO] when a dynamic route matches; null otherwise so callers can
     *         fall back to static routes or 404 handling.
     */
    fun handleDynamic(
        requestDTO: RequestDTO,
        query: Map<String, String>,
    ): ResponseDTO? {
        val requestTarget = requestDTO.target
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
                } else if (segmentRegex.matches(targetValue) || optionalSegment.matches(targetValue)) {
                    val match =
                        if (segmentRegex.matches(targetValue)) {
                            segmentRegex.matchEntire(targetValue)!!.groupValues[1]
                        } else {
                            optionalSegment.matchEntire(targetValue)!!.groupValues[1]
                        }
                    dynamics[match] = url[i]
                } else {
                    return@forEach
                }
            }

            route._data = dynamics
            route.request = requestDTO
            route.queries = query

            val response = route.middlewareProcessBefore(requestDTO.toResult())
            val produced = response ?: route.content()

            return produced
        }
        return null
    }

    /**
     * Returns a [ResponseDTO] for a static [page], [Page.content] is evaluated.
     */
    fun handleResponse(
        page: Page,
        clientHandler: ClientHandler,
        target: String,
    ): ResponseDTO = page.content()
}
