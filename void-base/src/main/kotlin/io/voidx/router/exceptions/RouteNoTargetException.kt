package io.voidx.router.exceptions

/**
 * Thrown when a page route target does not start with a leading slash ("/") or is malformed.
 */
class RouteNoTargetException(
    target: String,
) : Exception("$target is incorrectly formatted/doesn't being with /")
