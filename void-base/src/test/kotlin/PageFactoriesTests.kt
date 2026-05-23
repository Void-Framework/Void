package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.buildResponse
import io.voidx.page.exceptionPage
import io.voidx.page.notFoundPage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PageFactoriesTests {
    @Test
    fun exception_page_factory_executes_block_and_returns_response() {
        val p =
            exceptionPage { _, _, _ ->
                // simulate reading exception if needed; here we just return a response
                buildResponse<String> {
                    status = 500
                    statusText = "Internal Server Error"
                    headers["Content-Type"] = "text/plain"
                    body = "oops"
                }
            }

        val req =
            io.voidx.dto.buildRequest { }.apply {
                attributes["exception"] = Exception()
            }
        val resp = p.content(req, emptyMap())
        assertEquals("Internal Server Error", resp.statusText)
        assertTrue(resp.body is ResponseBody.StringBody)
        assertEquals("oops", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun not_found_page_factory_executes_block_and_returns_response() {
        val p =
            notFoundPage { _, _ ->
                buildResponse<String> {
                    status = 404
                    statusText = "Not Found"
                    headers["Content-Type"] = "text/plain"
                    body = "missing"
                }
            }

        val req = io.voidx.dto.buildRequest { }
        val resp = p.content(req, emptyMap())
        assertEquals(404, resp.status)
        assertEquals("Not Found", resp.statusText)
        assertTrue(resp.body is ResponseBody.StringBody)
        assertEquals("missing", (resp.body as ResponseBody.StringBody).body)
    }
}
