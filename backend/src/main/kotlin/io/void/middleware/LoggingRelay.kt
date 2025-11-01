package io.void.middleware

import io.void.dto.http.RequestDTO
import java.util.UUID

val logBefore = relayBefore { request ->
    val req = request.getOrNull() ?: return@relayBefore null
    val traceId = req.headers["X-Trace-Id"] ?: UUID.randomUUID().toString()
    req.attributes["traceId"] = traceId
    return@relayBefore null
}

var RequestDTO.traceId: String
    get() = attributes["traceId"] as? String ?: ""
    set(value) { attributes["traceId"] = value }
