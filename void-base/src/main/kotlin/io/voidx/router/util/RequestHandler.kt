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
     * Resolve the incoming request against registered dynamic routes and produce a response for the first matching route.
     *
     * Dynamic route segments use `{name}` to capture a single path token and `{name?}` for an optional trailing segment; static segments must match exactly and a single trailing slash on either side is ignored. When a match is found the page's `_data`, `request`, and `queries` are populated, the page's "before" middleware is executed, and the page content is returned.
     *
     * @param requestDTO The incoming request to match.
     * @param query Map of query parameters to attach to the matched page.
     * @return A `ResponseDTO` for the matched dynamic route, or `null` if no dynamic route matches.
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