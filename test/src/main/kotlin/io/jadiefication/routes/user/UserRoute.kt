package io.jadiefication.routes.user

import io.void.dto.http.buildResponse
import io.void.dto.http.ok
import io.void.html.page.dynamic.dynamicApiRoute
import io.void.json.toJson
import kotlinx.serialization.Serializable

@Serializable
private data class UserDTO(
    val id: String,
    val name: String,
    val email: String,
)

val userRoute =
    dynamicApiRoute("/users/{id}/{name?}") {
        val userId = this["id"]!!
        val name = this["name?"]
        if (!userId.matches(Regex("\\d+"))) {
            buildResponse<String> {
                status = 404
                statusText = "Not Found"
                headers["Content-Type"] = "application/json"
                body = """{"error":"Invalid user ID"}"""
            }
        } else {
            val payload =
                UserDTO(
                    id = userId,
                    name = "User $userId",
                    email = "${name ?: "user"}$userId@example.com",
                )
            ok<String>(
                body = payload.toJson().getOrThrow(),
                headers = mutableMapOf("Content-Type" to "application/json"),
            )
        }
    }
