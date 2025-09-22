package io.jadiefication.routes.user

import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.dynamic.dynamicJsonRoute
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

val userRoute =
    dynamicJsonRoute("/users/{id}/{name?}") {
        val userId = this["id"]!!
        val name = this["name?"]
        if (!userId.matches(Regex("\\d+"))) {
            buildResponse {
                status = 404
                statusText = "Not Found"
                headers { put("Content-Type", "application/json") }
                body = """{"error":"Invalid user ID"}"""
            }
        } else {
            ResponseDTO.json(
                mapOf(
                    "id" to userId,
                    "name" to "User $userId",
                    "email" to "${name ?: "user"}$userId@example.com",
                ),
                200,
                "OK",
            )
        }
    }
