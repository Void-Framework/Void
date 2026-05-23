package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.dto.buildResponse
import io.voidx.handle
import io.voidx.page.BadRequestPage
import io.voidx.page.badRequestPage
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class TestBadRequestSocket(
    private val incoming: ByteArray,
) : Socket() {
    constructor(raw: String) : this(raw.toByteArray())

    private val inBytes = ByteArrayInputStream(incoming)
    private val outBytes = ByteArrayOutputStream()
    private var closed = false

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    override fun close() { closed = true }

    fun text(): String = outBytes.toString().replace("\r\n", "\n")

    fun wasClosed(): Boolean = closed
}

/**
 * Tests for the new BadRequestPage class and badRequestPage factory function,
 * plus the Router.badRequestPage instance variable.
 */
class BadRequestPageTests {

    @Test
    fun bad_request_page_factory_creates_valid_page() {
        val page =
            badRequestPage { _, _ ->
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    headers["Content-Type"] = "text/plain"
                    body = "invalid request"
                }
            }

        assertNotNull(page, "Factory should return a non-null page")
        assertTrue(page is BadRequestPage, "Factory should return a BadRequestPage")
    }

    @Test
    fun bad_request_page_factory_block_produces_correct_response() {
        val page =
            badRequestPage { req, queries ->
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    headers["Content-Type"] = "application/json"
                    body = """{"error":"bad request","path":"${req.target}"}"""
                }
            }

        val req = buildRequest { target = "/bad" }
        val resp = page.content(req, emptyMap())

        assertEquals(400, resp.status)
        assertEquals("Bad Request", resp.statusText)
        assertEquals("application/json", resp.headers["Content-Type"])
        assertTrue((resp.body as ResponseBody.StringBody).body.contains("bad request"))
        assertTrue((resp.body as ResponseBody.StringBody).body.contains("/bad"))
    }

    @Test
    fun bad_request_page_factory_receives_query_parameters() {
        var capturedQueries: Map<String, String>? = null

        val page =
            badRequestPage { _, queries ->
                capturedQueries = queries
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    body = "bad"
                }
            }

        val req = buildRequest { }
        page.content(req, mapOf("debug" to "true", "version" to "2"))

        assertEquals(mapOf("debug" to "true", "version" to "2"), capturedQueries)
    }

    @Test
    fun router_bad_request_page_can_be_replaced_with_custom_page() {
        val r = router { }
        val customPage =
            badRequestPage { _, _ ->
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    headers["Content-Type"] = "text/plain"
                    body = "custom-400-response"
                }
            }

        r.badRequestPage = customPage
        assertEquals(customPage, r.badRequestPage)
    }

    @Test
    fun router_bad_request_page_produces_correct_response_via_socket() {
        val r = router { }
        r.badRequestPage =
            badRequestPage { _, _ ->
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    headers["Content-Type"] = "text/plain"
                    body = "intercepted-bad-request"
                }
            }

        // Empty byte array triggers Malformed=true on parse
        val sock = TestBadRequestSocket(ByteArray(0))
        sock.handle(1.1, r)

        val output = sock.text()
        assertTrue(output.contains("intercepted-bad-request"), "Expected custom bad request body but got:\n$output")
    }

    @Test
    fun bad_request_page_content_receives_request_from_attributes() {
        var capturedTarget: String? = null

        val page =
            badRequestPage { req, _ ->
                capturedTarget = req.target
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    body = "bad"
                }
            }

        val req = buildRequest { target = "/some/path" }
        page.content(req, emptyMap())

        assertEquals("/some/path", capturedTarget)
    }

    @Test
    fun default_router_bad_request_page_returns_400_status() {
        val r = router { }
        val req = buildRequest { target = "/" }

        // Default badRequestPage should return a 400 response
        val resp = r.badRequestPage.content(req, emptyMap())
        assertEquals(400, resp.status)
    }
}