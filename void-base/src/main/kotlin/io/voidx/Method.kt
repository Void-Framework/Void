package io.voidx

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

    /** Brews coffee on the HTCPCP server. Using POST is also allowed but deprecated;
     *  a proposed "Accept-Additions" header can request extras like Cream, Vanilla, or Whisky. */
    BREW,

    /** Retrieves metadata about the coffee, e.g., its properties and current state. */
    PROPFIND,

    /** A playful method causing the teapot to stop pouring milk, used for testing or jokes. */
    WHEN,
}
