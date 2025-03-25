package io.void.api

import io.void.api.method.Method
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page
import io.void.http.builder.HTTPBuilder
import java.util.UUID

class CssPage(uuid: UUID, val body: String) : ApiPage(
    target = "/css/$uuid/styles.css",
    method = Method.GET
) {

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