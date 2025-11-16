package io.voidx.middleware

import io.voidx.dto.http.RequestDTO
import org.slf4j.Logger
import java.util.*

/**
 * Logging middleware utilities.
 *
 * - [logBefore]: BEFORE relay that ensures a per-request trace ID is present. If the incoming request
 *   includes an `X-Trace-Id` header, that value is used; otherwise, a new UUID is generated and stored
 *   in [RequestDTO.attributes] under the `traceId` key. Use [RequestDTO.traceId] to access it.
 * - [logAfter]: AFTER relay registrar to emit a single structured log line for each handled request.
 */
val logBefore =
    relayBefore { request ->
        val req = request.getOrNull() ?: return@relayBefore null
        val traceId = req.headers["X-Trace-Id"] ?: UUID.randomUUID().toString()
        req.attributes["traceId"] = traceId
        return@relayBefore null
    }

/** Convenience extension to get/set the trace ID bound to this request. */
var RequestDTO.traceId: String
    get() = attributes["traceId"] as? String ?: headers["X-Trace-Id"] ?: ""
    set(value) {
        attributes["traceId"] = value
    }

/**
 * AFTER middleware that logs each HTTP request and response using the given [logger].
 * Logs the trace ID, method, target, and response status.
 */
fun logAfter(logger: Logger) =
    relayAfter { result ->
        val response = result.getOrNull() ?: return@relayAfter
        val request = response.request
        val traceId = request.traceId

        logger.info("[{}] {} {} -> {}", traceId, request.method, request.target, response.status)
    }
