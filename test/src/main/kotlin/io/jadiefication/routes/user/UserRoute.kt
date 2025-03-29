package io.jadiefication.routes.user

import io.void.dto.ResponseDTO
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import kotlin.reflect.KClass

class UserRoute : DynamicPage<ContentType.Response>(target = "/users/{id}") {
    override val metadata: Metadata? = null
    override val contentType: KClass<ContentType.Response> = ContentType.Response::class

    override fun content(): ContentType.Response {
        val userId = request.target.split("/").last()
        
        // Validate userId is numeric
        if (!userId.matches(Regex("\\d+"))) {
            return ContentType.Response(ResponseDTO(
                status = 404,
                statusText = "Not Found",
                headers = mutableMapOf("Content-Type" to "application/json"),
                body = """{"error": "Invalid user ID"}"""
            ))
        }

        return ContentType.Response(ResponseDTO.json(
            mutableMapOf(
                "id" to userId,
                "name" to "User $userId",
                "email" to "user$userId@example.com",
                "createdAt" to System.currentTimeMillis()
            ),
            200,
            "OK"
        ))
    }
}