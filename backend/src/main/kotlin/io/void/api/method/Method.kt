package io.void.api.method

/**
 * Standard HTTP request methods supported by the framework.
 *
 * These are used throughout routing and request parsing to determine the
 * intended action for an incoming request.
 */
enum class Method {
    /** Retrieve a representation of the resource. */
    GET,

    /** Same as GET but without a response body. */
    HEAD,

    /** Submit an entity to the resource. */
    POST,

    /** Replace the resource with the request payload. */
    PUT,

    /** Remove the specified resource. */
    DELETE,

    /** Establish a tunnel to the server. */
    CONNECT,

    /** Describe the communication options. */
    OPTIONS,

    /** Perform a message loop-back test. */
    TRACE,

    /** Apply partial modifications to a resource. */
    PATCH,
}
