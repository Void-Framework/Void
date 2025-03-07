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
                "name" to "Jade",
                "age" to 20,
                "isStudent" to true,
                "grades" to listOf(90, 85, 88),
                "meta" to mapOf(
                    "registered" to true,
                    "languages" to listOf("Kotlin", "Java", "Python"),
                    "profile" to mapOf(
                        "bio" to "Self-taught programmer",
                        "socials" to mapOf(
                            "github" to "https://github.com/jade",
                            "twitter" to "@jade_code"
                        )
                    )
                ),
                "nullValue" to null,
                "emptyList" to listOf<String>(),
                "emptyMap" to mapOf<String, Any>()
            ), 200, "All is fine")
        } else {
            throw MalformedMethodException("Incorrect method used in HTTP request")
        }
    }
}