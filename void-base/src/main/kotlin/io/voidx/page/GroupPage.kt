package io.voidx.page

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.emptyResponse
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.util.toResult
import kotlin.collections.find

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
    override var request: RequestDTO
        get() = super.request
        set(value) {
            super.request = value
            routes.forEach { it.request = value }
        }
    internal val routes = mutableListOf<PageHandler>()

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

    private fun findProperRoute(): PageHandler? {
        if (target == request.target) return this

        return routes
            .sortedByDescending { it.target.length }
            .firstNotNullOfOrNull { child ->
                when (child) {
                    is GroupPage -> {
                        if (child.target == request.target) {
                            child
                        } else if (request.target.startsWith(child.target) &&
                            (request.target.length == child.target.length ||
                                    request.target[child.target.length] == '/')) {
                            child.findProperRoute()
                        } else {
                            null
                        }
                    }
                    else -> if (child.target == request.target) child else null
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

        return handledByChild
            ?: responses[request.method]?.invoke(request)
            ?: emptyResponse()
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