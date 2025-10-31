package io.jadiefication.routes.echo

import io.void.dto.http.ok
import io.void.html.page.apiRoute
import io.void.json.toJson
import kotlinx.serialization.Serializable

@Serializable
private data class EchoResponse(
    val path: String,
    val query: Map<String, String>,
    val hasQuery: Boolean,
)

// Simple route that echoes back parsed query string from the raw target
// This demonstrates how RequestDTO.target can be used to parse queries for jsonRoute
val echoRoute =
    apiRoute("/echo") { request ->
        val payload =
            EchoResponse(
                path = request.target.substringBefore("?"),
                query = queries,
                hasQuery = queries.isNotEmpty(),
            )
        ok(
            body = payload.toJson().getOrThrow(),
            headers = mutableMapOf("Content-Type" to "application/json"),
        )
    }
