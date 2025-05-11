package io.jadiefication.routes.setter

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

class SetterRoute : Page<ContentType.Response>(
    target = "/setter"
) {

    override var metadata: Metadata? = null

    private val method = Method.GET
    override val contentType: KClass<ContentType.Response> = ContentType.Response::class

    override fun content(): ContentType.Response {
        return ContentType.Response(responseDTO = if (request.method == method) {
            ResponseDTO.json(mutableMapOf(
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
            ResponseDTO(
                status = 405,
                statusText = "Method not allowed",
                headers = mutableMapOf(),
                body = ""
            )
        })
    }
}