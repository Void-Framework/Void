package io.voidx.css

import io.voidx.Method
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.dto.headers
import io.voidx.page.Page
import java.util.UUID

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
