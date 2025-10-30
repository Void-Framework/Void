package io.jadiefication.routes.echo

import io.void.dto.http.ResponseDTO
import io.void.html.page.apiRoute

// Simple route that echoes back parsed query string from the raw target
// This demonstrates how RequestDTO.target can be used to parse queries for jsonRoute
val echoRoute =
    apiRoute("/echo") { request ->
        ResponseDTO.json(
            mapOf(
                "path" to request.target.substringBefore("?"),
                "query" to queries,
                "hasQuery" to queries.isNotEmpty(),
            ),
            200,
            "OK",
        )
    }
