package io.voidx.middleware

import io.voidx.Method
import io.voidx.dto.buildResponse
import io.voidx.page.Page

/**
 * Registers CORS middleware on this Page to handle OPTIONS preflight requests and to attach
 * Access-Control-Allow-* headers to normal responses.
 *
 * When a request is an OPTIONS preflight, the middleware short-circuits with a 200 OK response
 * and appropriate CORS headers. For non-OPTIONS requests, the middleware adds the same CORS
 * headers to the outgoing response when the origin is allowed.
 *
 * @param allowedOrigins Set of allowed Origin values. If `null`, the middleware uses the wildcard
 *   `"*"` for Access-Control-Allow-Origin; if non-null, only requests whose Origin exactly matches
 *   an entry in the set will receive CORS headers.
 */
fun Page.corsMiddleware(allowedOrigins: Set<String>? = null) {
    before(
        relayBefore { pReq ->
            val req = pReq.getOrThrow()
            val origin = req["Origin"]
            val allowOrigin =
                when {
                    allowedOrigins == null -> "*"
                    origin != null && origin in allowedOrigins -> origin
                    else -> null
                }

            if (req.method == Method.OPTIONS) {
                val headers = mutableMapOf<String, String>()
                if (allowOrigin != null) {
                    headers["Access-Control-Allow-Origin"] = allowOrigin
                    headers["Access-Control-Allow-Methods"] = "GET, POST, PUT, DELETE, OPTIONS"
                    headers["Access-Control-Allow-Headers"] = "*"
                    headers["Access-Control-Allow-Credentials"] = "true"
                }
                return@relayBefore buildResponse {
                    status = 200
                    statusText = "OK"
                    body = ""
                    this.headers = headers
                }
            }

            null // continue normal request
        },
    )

    // attach headers after normal request
    after(
        relayAfter { pResp ->
            val resp = pResp.getOrThrow()
            val origin = resp.request["Origin"]
            val allowOrigin =
                when {
                    allowedOrigins == null -> "*"
                    origin != null && origin in allowedOrigins -> origin
                    else -> null
                }

            if (allowOrigin != null) {
                resp.headers["Access-Control-Allow-Origin"] = allowOrigin
                resp.headers["Access-Control-Allow-Methods"] = "GET, POST, PUT, DELETE, OPTIONS"
                resp.headers["Access-Control-Allow-Headers"] = "*"
                resp.headers["Access-Control-Allow-Credentials"] = "true"
            }
        },
    )
}