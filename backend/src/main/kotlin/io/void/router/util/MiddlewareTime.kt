package io.void.router.util

/** Indicates when a middleware should execute relative to the route handler. */
enum class MiddlewareTime {
    BEFORE,
    AFTER,
}
