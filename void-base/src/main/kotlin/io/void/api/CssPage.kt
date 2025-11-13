package io.void.api

import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.Page
import java.util.*

/**
 * Internal page that serves a generated CSS asset at a unique UUID-backed path.
 * Used by the router to expose styles discovered under resources/css and added to page metadata.
 */
class CssPage(
    uuid: UUID,
    private val body: String,
) : Page(
        target = "/css/$uuid/styles.css",
    ) {

    override fun content(): ResponseDTO =
        if (request.method == Method.GET) {
            buildResponse {
                status = 200
                statusText = "All is Well"
                headers {
                    put("Content-Type", "text/css")
                }
                this.body = this@CssPage.body
            }
        } else {
            buildResponse<String> {
                status = 405
                statusText = "Method not allowed"
            }
        }
}
