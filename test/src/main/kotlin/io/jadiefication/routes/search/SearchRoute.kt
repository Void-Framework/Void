package io.jadiefication.routes.search

import io.void.dto.http.ResponseDTO
import io.void.html.page.apiRoute
import io.void.html.page.dynamic.dynamicApiRoute

// Dynamic route with optional segment that also echoes back query parameters
val searchRoute =
    dynamicApiRoute("/search/{category?}") { request ->
        val category = this["category?"]?.substringBefore("?")
        ResponseDTO.json(
            mapOf(
                "category" to category,
                "path" to request.target.substringBefore("?"),
                "query" to queries,
            ),
            200,
            "OK",
        )
    }

// Static variant to support query-only searches without optional segment
val searchRootRoute =
    apiRoute("/search") { request ->
        ResponseDTO.json(
            mapOf(
                "category" to null,
                "path" to target.substringBefore("?"),
                "query" to queries,
            ),
            200,
            "OK",
        )
    }
