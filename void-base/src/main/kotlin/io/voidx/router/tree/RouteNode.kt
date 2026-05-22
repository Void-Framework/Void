package io.voidx.router.tree

import io.voidx.page.DynamicPage
import io.voidx.page.Page
import io.voidx.page.PageHandler
import io.voidx.router.exceptions.RouteTargetUsedException

/**
 * Represents a node in the routing tree.
 * Handles both static segments and dynamic parameters.
 *
 * @property optional Whether this node represents an optional path segment.
 * @property paramName The name of the dynamic parameter, if this is a dynamic node.
 */
internal class RouteNode(
    val optional: Boolean = false,
    val paramName: String? = null,
) {
    /** Map of static child segments to their respective nodes. */
    val staticChildren = HashMap<String, RouteNode>()

    /** The dynamic child node, if any. */
    var dynamicChild: RouteNode? = null

    /** The page handler associated with this route, if it's a leaf node. */
    var handler: Page? = null

    /**
     * Inserts a route into the tree.
     *
     * @param segments The path segments to insert.
     * @param index The current segment index being processed.
     * @param page The [Page] to associate with this route.
     * @throws RouteTargetUsedException if the route is already registered.
     */
    fun insert(
        segments: List<String>,
        index: Int,
        page: Page,
    ) {
        if (index == segments.size) {
            if (handler != null) {
                throw RouteTargetUsedException(page.target)
            }
            handler = page
            return
        }

        val part = segments[index]

        val isDynamic = part.matches(DYNAMIC_REGEX)
        val isOptional = part.matches(OPTIONAL_REGEX)

        val node =
            when {
                isOptional -> {
                    if (dynamicChild == null) {
                        val name = extractParamName(part)
                        dynamicChild =
                            RouteNode(
                                paramName = name,
                                optional = true,
                            )
                    }
                    dynamicChild!!
                }

                isDynamic || isOptional -> {
                    if (dynamicChild == null) {
                        val name = extractParamName(part)
                        dynamicChild =
                            RouteNode(
                                paramName = name,
                            )
                    }
                    dynamicChild!!
                }

                else -> {
                    staticChildren.getOrPut(part) {
                        RouteNode()
                    }
                }
            }

        node.insert(segments, index + 1, page)
    }

    /**
     * Attempts to match a list of path segments against the tree.
     *
     * @param segments The path segments from the request URI.
     * @param index The current segment index being matched.
     * @param params A mutable map to collect dynamic path parameters.
     * @return The matched [Page], or null if no match is found.
     */
    fun match(
        segments: List<String>,
        index: Int,
        params: MutableMap<String, String>,
    ): Page? {
        if (index == segments.size) {
            if (dynamicChild != null && dynamicChild!!.optional) {
                params[dynamicChild!!.paramName ?: ""] = ""
                if (handler is DynamicPage) {
                    (handler as DynamicPage)._data.putAll(params)
                }
                return dynamicChild!!.handler
            }
            if (handler is DynamicPage) {
                (handler as DynamicPage)._data.putAll(params)
            }
            return handler
        }

        val part = segments[index]

        // 1. static first
        staticChildren[part]?.match(segments, index + 1, params)?.let {
            return it
        }

        // 2. dynamic second
        dynamicChild?.let { dyn ->
            params[dyn.paramName ?: ""] = part
            dyn.match(segments, index + 1, params)?.let {
                if (it is DynamicPage) {
                    it._data.putAll(params)
                }
                return it
            }
            params.remove(dyn.paramName ?: "")
        }

        return handler
    }

    companion object {
        /** Matches a required dynamic segment like `{id}`. */
        private val DYNAMIC_REGEX =
            Regex("^\\{([a-zA-Z_][a-zA-Z0-9_]*)}$")

        /** Matches an optional dynamic segment like `{slug?}`. */
        private val OPTIONAL_REGEX =
            Regex("^\\{([a-zA-Z_][a-zA-Z0-9_]*)\\?\\}$")

        /**
         * Extracts the parameter name from a segment string if it matches dynamic or optional patterns.
         */
        private fun extractParamName(part: String): String =
            DYNAMIC_REGEX.matchEntire(part)?.groupValues?.get(1)
                ?: OPTIONAL_REGEX.matchEntire(part)?.groupValues?.get(1)
                ?: ""
    }
}
