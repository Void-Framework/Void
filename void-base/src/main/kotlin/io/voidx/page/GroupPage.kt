package io.voidx.page

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.router.util.RouteCheck
import io.voidx.util.toResult
import io.voidx.util.trimTrailingEmpty

/**
 * Lightweight page container that supports route grouping and hierarchical dispatching.
 *
 * `GroupPage` allows organizing related routes under a common path prefix. It extends [PageHandler],
 * meaning it can handle HTTP methods directly while also delegating to nested routes.
 *
 * Typical usage involves using [groupRoute] or [group] to define a hierarchy:
 * ```kotlin
 * groupRoute("/api") {
 *     group("/v1") {
 *         GET { ok("version 1") }
 *     }
 * }
 * ```
 *
 * Behavior and Guarantees:
 * - Nested routes inherit middleware ([relaysBefore], [relaysAfter]) from their parent group at the time of creation.
 * - When [content] is called, it first attempts to delegate to a child route that matches the [request] target.
 * - If no child matches, it falls back to method handlers registered on the group itself.
 * - [target] paths are concatenated: a group "/api" with a sub-group "/v1" results in a target of "/api/v1".
 *
 * @param target The base path prefix for this group (e.g., "/api").
 */
class GroupPage(
    override val target: String,
) : PageHandler(target) {
    /**
     * The current request for this page and all nested routes.
     *
     * Updating this request automatically propagates the new value to every child route registered in [routes].
     */
    override var request: RequestDTO
        get() = super.request
        internal set(value) {
            super.request = value
            routes.forEach { it.request = value }
        }

    /** The collection of nested [PageHandler] routes registered within this group. */
    internal val routes = mutableListOf<PageHandler>()

    private val segmentRegex = Regex("""^\{([^{}]+)}$""")
    private val optionalSegment = Regex("""^\{([^{}?]+)\?}$""")

    /**
     * Attempts to match a route target pattern against an actual request target path.
     *
     * This method splits both paths into segments and compares them. It supports:
     * - Literal segment matching (e.g., "users" matches "users").
     * - Required dynamic segments (e.g., "{id}" matches "123").
     * - Optional trailing segments (e.g., "{slug?}" matches "my-post" or is omitted if the request path is shorter).
     *
     * If a match is successful, it extracts all dynamic segment values into a map.
     *
     * @param routeTarget The template path defined in the route (may contain curly braces for dynamic segments).
     * @param requestTarget The actual path from the HTTP request.
     * @return A map of parameter names to values if the path matches, or `null` if it doesn't.
     */
    private fun match(
        routeTarget: String,
        requestTarget: String,
    ): Map<String, String>? {
        val route = routeTarget.split('/').toMutableList().apply { trimTrailingEmpty() }
        val url = requestTarget.split('/').toMutableList().apply { trimTrailingEmpty() }

        val params = mutableMapOf<String, String>()

        if (route.size != url.size) {
            if (route.lastOrNull()?.matches(optionalSegment) == true &&
                route.size == url.size + 1
            ) {
                route.removeLast()
            } else {
                return null
            }
        }

        for (i in route.indices) {
            val r = route[i]
            val u = url[i]

            when {
                r == u -> {
                    Unit
                }

                segmentRegex.matches(r) -> {
                    params[segmentRegex.matchEntire(r)!!.groupValues[1]] = u
                }

                optionalSegment.matches(r) -> {
                    params[optionalSegment.matchEntire(r)!!.groupValues[1]] = u
                }

                else -> {
                    return null
                }
            }
        }

        return params
    }

    /**
     * Creates and registers a nested GroupPage under the current page using the given path.
     *
     * Creates a new GroupPage whose target is the current page's target concatenated with `path`,
     * applies `builder` to configure the new page, inherits this page's `relaysBefore` and
     * `relaysAfter` into the new page, and adds the new page to the current page's route set.
     *
     * @param path The suffix to append to the current target to form the subpage's target.
     * @param builder Configuration block applied to the newly created subpage.
     */
    fun group(
        path: String,
        builder: GroupPage.() -> Unit,
    ) {
        val page = GroupPage("$target$path")
        page.builder()
        page.relaysBefore += this.relaysBefore
        page.relaysAfter += this.relaysAfter
        routes.add(page)
    }

    /**
     * Executes the BEFORE middleware chain for the most specific matching route.
     *
     * This method resolves the target route for the current request. If the resolved route is a
     * leaf node (not a GroupPage), it delegates middleware processing to that route. Otherwise,
     * it executes the [relaysBefore] registered on the matched group.
     *
     * @return The first [ResponseDTO] produced by a middleware, or `null` if none intercepted the request.
     */
    override fun middlewareProcessBefore(): ResponseDTO? {
        val targetRoute = findProperRoute()

        if (targetRoute != null && targetRoute !is GroupPage) {
            return targetRoute.middlewareProcessBefore()
        } else {
            targetRoute?.relaysBefore?.forEach {
                val newResponse = (it as? RelayBefore)?.processBefore(request.toResult())
                if (newResponse != null) {
                    newResponse._request = request
                    return newResponse
                }
            }
        }
        return null
    }

    /**
     * Executes the AFTER middleware chain for the most specific matching route.
     *
     * Resolves the target route for the current request and delegates the processing of [response]
     * to it if it's a leaf node. If the matched route is a group, it executes that group's
     * [relaysAfter] chain.
     *
     * @param response The [Result] containing either the [ResponseDTO] or an exception.
     */
    override fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        val targetRoute = findProperRoute()
        if (targetRoute != null && targetRoute !is GroupPage) {
            targetRoute.middlewareProcessAfter(response)
        } else {
            targetRoute?.relaysAfter?.forEach {
                (it as? RelayAfter)?.processAfter(response)
            }
        }
    }

    /**
     * Identifies the most specific route matching the current request's target path.
     *
     * Performs a recursive search through the registered [routes], prioritizing longer (more specific)
     * paths. It ensures that path matches occur only at segment boundaries (i.e., matching "/api/v1"
     * against "/api/v1/users" but not against "/api/v1-legacy").
     *
     * @return The [PageHandler] that best matches the request path, or `null` if no match is found.
     */
    private fun findProperRoute(): PageHandler? {
        if (target == request.target) return this

        return routes
            .sortedByDescending { it.target.length }
            .firstNotNullOfOrNull { child ->
                when (child) {
                    is GroupPage -> {
                        if (match(child.target, request.target) != null) {
                            child
                        } else if (request.target.startsWith(child.target) &&
                            (
                                request.target.length == child.target.length ||
                                    request.target[child.target.length] == '/'
                            )
                        ) {
                            child.findProperRoute()
                        } else {
                            null
                        }
                    }

                    else -> {
                        if (match(child.target, request.target) != null) child else null
                    }
                }
            }
    }

    /**
     * Resolve and return the response for the current request by delegating to a matching child route, a registered method handler, or an empty response.
     *
     * @return The resolved ResponseDTO: the response produced by a matching child route if one handles the request, the response from a handler registered for the request method if present, or an empty response otherwise.
     */
    override fun content(): ResponseDTO {
        val route = findProperRoute()
        val handledByChild = if (route == this) super.content() else route?.content()
        val isThisIt = route == this && match(target, request.target) != null

        return handledByChild
            ?: if (isThisIt) {
                responses[request.method]?.invoke(request)
            } else {
                RouteCheck.nullPage.apply { this.request = this@GroupPage.request }.content()
            }!!
    }
}

/**
 * Create a GroupPage with the specified path and apply the provided configuration block to it.
 *
 * @param path The base target path for the created group.
 * @param block Configuration to run on the new GroupPage.
 * @return The configured GroupPage.
 */
fun groupRoute(
    path: String,
    block: GroupPage.() -> Unit,
): GroupPage {
    val page = GroupPage(path)
    page.block()
    return page
}
