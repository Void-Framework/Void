package io.jadiefication.routes.search

import io.voidx.dto.http.ok
import io.voidx.html.page.apiRoute
import io.voidx.html.page.dynamic.dynamicApiRoute
import io.voidx.json.toJson
import kotlinx.serialization.Serializable

@Serializable
private data class SearchResponse(
    val path: String,
    val category: String? = null,
    val query: Map<String, String>,
)

// Dynamic route with optional segment that also echoes back query parameters
val searchRoute =
    dynamicApiRoute("/search/{category?}") { request ->
        val category = this["category?"]?.substringBefore("?")
        val payload =
            SearchResponse(
                path = request.target.substringBefore("?"),
                category = category,
                query = queries,
            )
        ok(
            body = payload.toJson().getOrThrow(),
            headers = mutableMapOf("Content-Type" to "application/json"),
        )
    }

// Static variant to support query-only searches without optional segment
val searchRootRoute =
    apiRoute("/search") { request ->
        val payload =
            SearchResponse(
                path = target.substringBefore("?"),
                category = null,
                query = queries,
            )
        ok(
            body = payload.toJson().getOrThrow(),
            headers = mutableMapOf("Content-Type" to "application/json"),
        )
    }
