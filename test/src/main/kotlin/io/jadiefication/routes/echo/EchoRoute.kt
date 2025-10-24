package io.jadiefication.routes.echo

import io.void.dto.http.ResponseDTO
import io.void.html.page.jsonRoute

// Simple route that echoes back parsed query string from the raw target
// This demonstrates how RequestDTO.target can be used to parse queries for jsonRoute
val echoRoute =
    jsonRoute("/echo") { request ->
        val query =
            request.target
                .substringAfter("?", "")
                .split("&")
                .mapNotNull {
                    val parts = it.split("=", limit = 2)
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()

        ResponseDTO.json(
            mapOf(
                "path" to request.target.substringBefore("?"),
                "query" to query,
                "hasQuery" to query.isNotEmpty(),
            ),
            200,
            "OK",
        )
    }
