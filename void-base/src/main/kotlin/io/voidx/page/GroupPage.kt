package io.voidx.page

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.json.Negotiator
import io.voidx.router.CustomPages

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

    /** The collection of nested [PageHandler] routes registered within this group. */
    internal val routes = mutableListOf<PageHandler>()

    internal var flattened = false

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

    internal fun flatten(): List<PageHandler> {
        this.flattened = true
        val pages = mutableListOf<PageHandler>(this)
        routes.forEach {
            if (it is GroupPage) pages.addAll(it.also { it.flattened = true }.flatten())
            else pages.add(it)
        }
        return pages
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
