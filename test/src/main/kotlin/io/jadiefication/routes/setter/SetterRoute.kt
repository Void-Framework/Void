package io.jadiefication.routes.setter

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.html.page.jsonRoute

val setterRoute =
    jsonRoute("/setter") { request ->
        val method = Method.GET
        if (request.method ==
            method
        ) {
            ResponseDTO.json(
                mutableMapOf(
                    "name" to "Jade",
                    "age" to 20,
                    "isStudent" to true,
                    "grades" to listOf(90, 85, 88),
                    "meta" to
                        mapOf(
                            "registered" to true,
                            "languages" to listOf("Kotlin", "Java", "Python"),
                            "profile" to
                                mapOf(
                                    "bio" to "Self-taught programmer",
                                    "socials" to mapOf("github" to "https://github.com/jade", "twitter" to "@jade_code"),
                                ),
                        ),
                    "nullValue" to null,
                    "emptyList" to listOf<String>(),
                    "emptyMap" to mapOf<String, Any>(),
                ),
                200,
                "All is fine",
            )
        } else {
            buildResponse<String> {
                status = 405
                statusText = "Method not allowed"
            }
        }
    }
