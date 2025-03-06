package main.test.routes.setter

import main.api.ApiPage
import main.api.exception.MalformedMethodException
import main.api.method.Method
import main.java.main.DTO.RequestDTO
import main.java.main.DTO.ResponseDTO
import main.router.Router

class SetterRoute: ApiPage(
    target = "/setter",
    method = Method.GET
) {

    override fun serverGetter(request: RequestDTO): ResponseDTO {
        if (request.method == method) {
            return ResponseDTO.json(mapOf(
                "user" to mapOf(
                    "name" to "Jade",
                    "age" to 20,
                    "isStudent" to true,
                    "grades" to listOf(90, 85, 88),
                    "address" to mapOf(
                        "street" to "1234 Main St",
                        "city" to "Cityville",
                        "state" to "State",
                        "postalCode" to 12345
                    ),
                    "profile" to mapOf(
                        "bio" to "Self-taught programmer",
                        "socials" to mapOf(
                            "github" to "https://github.com/jade",
                            "twitter" to "@jade_code",
                            "linkedin" to "https://linkedin.com/in/jade"
                        ),
                        "languages" to listOf("Kotlin", "Java", "Python")
                    ),
                    "active" to true,
                    "nullField" to null,
                    "emptyList" to listOf<String>(),
                    "emptyMap" to mapOf<String, Any>()
                ),
                "system" to mapOf(
                    "os" to "Linux",
                    "version" to "5.10.0",
                    "architecture" to "x86_64",
                    "uptime" to 123456,
                    "maintenance" to mapOf(
                        "scheduled" to "2025-03-10T00:00:00Z",
                        "status" to "pending"
                    )
                ),
                "projects" to listOf(
                    mapOf(
                        "name" to "BladeBallMC",
                        "description" to "A competitive sports-based plugin for Minecraft",
                        "languages" to listOf("Kotlin", "Java"),
                        "status" to "active"
                    ),
                    mapOf(
                        "name" to "VoidFramework",
                        "description" to "A web framework in Kotlin for building dynamic applications",
                        "languages" to listOf("Kotlin", "HTML"),
                        "status" to "in development"
                    )
                ),
                "settings" to mapOf(
                    "theme" to "dark",
                    "notifications" to mapOf(
                        "email" to true,
                        "sms" to false
                    ),
                    "language" to "en-US"
                ),
                "logs" to listOf(
                    mapOf(
                        "timestamp" to "2025-03-05T12:00:00Z",
                        "level" to "INFO",
                        "message" to "Server started successfully"
                    ),
                    mapOf(
                        "timestamp" to "2025-03-05T12:05:00Z",
                        "level" to "ERROR",
                        "message" to "Failed to connect to database"
                    )
                )
            ), 200, "All is fine")
        } else {
            throw MalformedMethodException("Incorrect method used in HTTP request")
        }
    }
}