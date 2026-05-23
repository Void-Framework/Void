package io.voidx.page

typealias Path = String

/**
 * Page base for routes with dynamic path segments.
 *
 * Use curly braces to declare dynamic parameters:
 * - Required segment: {id}  e.g. /users/{id}
 * - Optional trailing segment: {slug?} (must be last) e.g. /blog/{slug?}
 *
 * Resolved values are exposed via queries in [content].
 */
abstract class DynamicPage(
    target: String,
) : Page(target = target)
