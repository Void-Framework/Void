package io.void.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildRequest
import io.void.html.page.Page
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

context(page: Page<*>)
fun logAfter(logger: Logger) {
    page.after(relayAfter {
        val response = it.getOrNull() ?: return@relayAfter
        val request = response.request
        val traceId = request.traceId

        logger.info("[{}] {} {} -> {}", traceId, request.method, request.target, response.status)
    })
}
