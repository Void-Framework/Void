package io.voidx.router.util

import io.voidx.ClientHandler
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.page.DynamicPage
import io.voidx.page.Page
import io.voidx.page.Path
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
     * Resolves an incoming request against registered dynamic routes and returns the response for the first matching route.
     *
     * Dynamic route segments use `{name}` to capture a single path token and `{name?}` for an optional trailing segment.
     * Static segments must match exactly; a single trailing empty segment (a trailing slash) on either side is ignored.
     *
     * When a route matches, the route's `_data` is populated with captured segments, `request` is set to `requestDTO`,
     * `queries` is set to `query`, the route's before-middleware is executed, and the middleware result is used if not null;
     * otherwise the route's content is returned.
     *
     * @param requestDTO The incoming request whose target path will be matched against dynamic routes.
     * @param query Query parameters to attach to the matched page.
     * @return A `ResponseDTO` produced by the matched route (middleware result or page content), or `null` if no route matches.
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

            val response = route.middlewareProcessBefore()
            val produced = response ?: route.content()

            return produced
        }
        return null
    }

    /**
     * Obtain the response for a static page by evaluating its content.
     *
     * @param page The static page whose content will be evaluated to build the response.
     * @param clientHandler The client handler associated with the request (provided for context).
     * @param target The request target path that led to this page.
     * @return The ResponseDTO produced by the page's content.
     */
    fun handleResponse(
        page: Page,
        clientHandler: ClientHandler,
        target: String,
    ): ResponseDTO = page.content()
}
