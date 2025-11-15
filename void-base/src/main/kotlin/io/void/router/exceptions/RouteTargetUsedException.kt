package io.void.router.exceptions

/** Thrown when a route target path is already registered by another page. */
class RouteTargetUsedException(
    target: String,
) : Exception("$target is already used in a different route")
