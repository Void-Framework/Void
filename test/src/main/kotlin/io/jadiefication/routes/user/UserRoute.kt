package io.jadiefication.routes.user

import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

class UserRoute : DynamicPage<ContentType.Response>(target = "/users/{id}/{name?}") {
    override var metadata: Metadata? = null
    override val contentType: KClass<ContentType.Response> = ContentType.Response::class

    override fun content(): ContentType.Response {
        val userId = data["id"]!!
        val name = data["name?"]

        // Validate userId is numeric
        userId.matches(Regex("\\d+")).let {
            if (!it) {
                return ContentType.Response(
                    buildResponse {
                        status = 404
                        statusText = "Not Found"
                        headers {
                            put("Content-Type", "application/json")
                        }
                        body = """{"error": "Invalid user ID"}"""
                    }
                )
            }
        }

        return ContentType.Response(
            ResponseDTO.json(
                mutableMapOf(
                    "id" to userId,
                    "name" to "User $userId",
                    "email" to "${name ?: "user"}$userId@example.com",
                    "createdAt" to System.currentTimeMillis(),
                ),
                200,
                "OK",
            ),
        )
    }
}
