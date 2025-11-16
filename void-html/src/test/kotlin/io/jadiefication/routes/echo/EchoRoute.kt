package io.jadiefication.routes.echo

import io.voidx.dto.http.ok
import io.voidx.html.page.apiRoute

// Simple route that echoes back parsed query string from the raw target
// This demonstrates how RequestDTO.target can be used to parse queries for jsonRoute
val echoRoute =
    apiRoute("/echo") { request ->
        val path = request.target.substringBefore("?")
        val hasQuery = queries.isNotEmpty()
        // Build a minimal JSON string without depending on void-json
        val queryJson =
            queries.entries.joinToString(
                prefix = "{",
                postfix = "}",
            ) { (k, v) -> "\"${k.replace("\"", "\\\"")}\":\"${v.replace("\"", "\\\"")}\"" }

        val payloadJson =
            """
            {"path":"$path","query":$queryJson,"hasQuery":$hasQuery}
            """.trimIndent()

        ok(
            body = payloadJson,
            headers = mutableMapOf("Content-Type" to "application/json"),
        )
    }
