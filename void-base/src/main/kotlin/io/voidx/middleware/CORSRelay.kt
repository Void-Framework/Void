package io.voidx.middleware

import io.voidx.Method
import io.voidx.dto.buildResponse
import io.voidx.page.Page

fun Page.corsMiddleware(allowedOrigins: Set<String>? = null) {
    before(
        relayBefore { pReq ->
            val req = pReq.getOrThrow()
            val origin = req["Origin"]
            val allowOrigin =
                when {
                    origin == null -> null
                    allowedOrigins == null -> "*"
                    origin in allowedOrigins -> origin
                    else -> null
                }
            val allowCredentials = allowOrigin != "*" && allowOrigin != null

            if (req.method == Method.OPTIONS) {
                val headers = mutableMapOf<String, String>()
                if (allowOrigin != null) {
                    headers["Access-Control-Allow-Origin"] = allowOrigin
                    headers["Access-Control-Allow-Methods"] = "GET, POST, PUT, DELETE, OPTIONS"
                    headers["Access-Control-Allow-Headers"] = "*"
                    if (allowCredentials) {
                        headers["Access-Control-Allow-Credentials"] = "true"
                    }
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
                    origin == null -> null
                    allowedOrigins == null -> "*"
                    origin in allowedOrigins -> origin
                    else -> null
                }
            val allowCredentials = allowOrigin != "*" && allowOrigin != null

            if (allowOrigin != null) {
                resp.headers["Access-Control-Allow-Origin"] = allowOrigin
                resp.headers["Access-Control-Allow-Methods"] = "GET, POST, PUT, DELETE, OPTIONS"
                resp.headers["Access-Control-Allow-Headers"] = "*"
                if (allowCredentials) {
                    resp.headers["Access-Control-Allow-Credentials"] = "true"
                }
            }
        },
    )
}
