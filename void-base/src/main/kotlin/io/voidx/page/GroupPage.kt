package io.voidx.page

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.emptyResponse

class GroupPage(
    override val target: String,
) : PageHandler(target) {
    override var request: RequestDTO
        get() = super.request
        set(value) {
            super.request = value
            routes.forEach { it.request = value }
        }
    private val routes = mutableSetOf<PageHandler>()

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
     * Resolve and return the response for the current request by delegating to a matching child route, a registered method handler, or an empty response.
     *
     * @return The resolved ResponseDTO: the response produced by a matching child route if one handles the request, the response from a handler registered for the request method if present, or an empty response otherwise.
     */
    override fun content(): ResponseDTO {
        val handledByChild =
            routes
                .find { child ->
                    when (child) {
                        is GroupPage -> child.target == request.target || request.target.startsWith(child.target)
                        else -> child.target == request.target
                    }
                }?.content()

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