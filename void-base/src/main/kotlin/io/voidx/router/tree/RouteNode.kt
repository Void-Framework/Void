package io.voidx.router.tree

import io.voidx.page.DynamicPage
import io.voidx.page.Page

internal class RouteNode(
    val dynamic: Boolean = false,
    val paramName: String? = null
) {
    val staticChildren = HashMap<String, RouteNode>()
    var dynamicChild: RouteNode? = null

    var handler: Page? = null

    fun insert(segments: List<String>, index: Int, page: Page) {
        if (index == segments.size) {
            handler = page
            return
        }

        val part = segments[index]

        val isDynamic = part.matches(DYNAMIC_REGEX)
        val isOptional = part.matches(OPTIONAL_REGEX)

        val node = when {
            isDynamic || isOptional -> {
                if (dynamicChild == null) {
                    val name = extractParamName(part)
                    dynamicChild = RouteNode(
                        dynamic = true,
                        paramName = name
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

    fun match(
        segments: List<String>,
        index: Int,
        params: MutableMap<String, String>
    ): Page? {
        if (index == segments.size) {
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
                return it
            }
            params.remove(dyn.paramName ?: "")
        }

        return handler
    }

    companion object {
        private val DYNAMIC_REGEX =
            Regex("^\\{([a-zA-Z_][a-zA-Z0-9_]*)}$")

        private val OPTIONAL_REGEX =
            Regex("^\\{([a-zA-Z_][a-zA-Z0-9_]*)\\?\\}$")

        private fun extractParamName(part: String): String {
            return DYNAMIC_REGEX.matchEntire(part)?.groupValues?.get(1)
                ?: OPTIONAL_REGEX.matchEntire(part)?.groupValues?.get(1)
                ?: ""
        }
    }
}