package io.jadiefication.routes.search

import io.void.dto.http.ResponseDTO
import io.void.html.page.dynamic.dynamicJsonRoute
import io.void.html.page.jsonRoute

// Dynamic route with optional segment that also echoes back query parameters
val searchRoute =
    dynamicJsonRoute("/search/{category?}") { request ->
        val category = this["category?"]?.substringBefore("?")
        val target = request.target
        val query =
            target
                .substringAfter("?", "")
                .split("&")
                .mapNotNull {
                    val parts = it.split("=", limit = 2)
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()

        ResponseDTO.json(
            mapOf(
                "category" to category,
                "path" to target.substringBefore("?"),
                "query" to query,
            ),
            200,
            "OK",
        )
    }

// Static variant to support query-only searches without optional segment
val searchRootRoute =
    jsonRoute("/search") { request ->
        val target = request.target
        val query =
            target
                .substringAfter("?", "")
                .split("&")
                .mapNotNull {
                    val parts = it.split("=", limit = 2)
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()
        ResponseDTO.json(
            mapOf(
                "category" to null,
                "path" to target.substringBefore("?"),
                "query" to query,
            ),
            200,
            "OK",
        )
    }
