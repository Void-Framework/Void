package io.jadiefication.routes.setter

import io.void.api.method.Method
import io.void.dto.http.buildResponse
import io.void.dto.http.ok
import io.void.html.page.apiRoute
import io.void.json.toJson
import kotlinx.serialization.Serializable

@Serializable
private data class SetterMeta(
    val registered: Boolean,
    val languages: List<String>,
    val profile: Map<String, String>,
)

@Serializable
private data class SetterDTO(
    val name: String,
    val age: Int,
    val isStudent: Boolean,
    val grades: List<Int>,
    val meta: SetterMeta,
    val nullValue: String? = null,
    val emptyList: List<String> = emptyList(),
    val emptyMap: Map<String, String> = emptyMap(),
)

val setterRoute =
    apiRoute("/setter") { request ->
        val method = Method.GET
        if (request.method == method) {
            val payload =
                SetterDTO(
                    name = "Jade",
                    age = 20,
                    isStudent = true,
                    grades = listOf(90, 85, 88),
                    meta =
                        SetterMeta(
                            registered = true,
                            languages = listOf("Kotlin", "Java", "Python"),
                            profile =
                                mapOf(
                                    "bio" to "Self-taught programmer",
                                    "github" to "https://github.com/jade",
                                    "twitter" to "@jade_code",
                                ),
                        ),
                    nullValue = null,
                    emptyList = listOf(),
                    emptyMap = mapOf(),
                )
            ok<String>(
                body = payload.toJson().getOrThrow(),
                headers = mutableMapOf("Content-Type" to "application/json"),
            )
        } else {
            buildResponse<String> {
                status = 405
                statusText = "Method not allowed"
            }
        }
    }
