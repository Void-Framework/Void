package io.void.api

import io.void.api.method.Method
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.js.JavaScript
import java.util.*

internal class CssPage(uuid: UUID, private val body: String) : ApiPage(
    target = "/css/$uuid/styles.css",
    method = Method.GET
) {

    override val javascript: JavaScript? = null

    override fun serverGetter(request: RequestDTO): ResponseDTO {
        return if (request.method == method) {
            ResponseDTO(
                status = 200,
                statusText = "All is Well",
                headers = mutableMapOf(
                    "Content-Type" to "text/css",
                ),
                body = body
            )
        } else {
            ResponseDTO(
                status = 405,
                statusText = "Method not allowed",
                headers = mutableMapOf(),
                body = ""
            )
        }
    }
}