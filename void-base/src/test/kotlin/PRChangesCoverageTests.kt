package test

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildRequest
import io.voidx.dto.buildResponse
import io.voidx.dto.ok
import io.voidx.handle
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.page.DynamicPage
import io.voidx.page.exceptionPage
import io.voidx.page.notFoundPage
import io.voidx.page.route
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail

private class SockPR(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes
    override fun getOutputStream(): OutputStream = outBytes
    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

/**
 * Additional targeted tests for PR-changed code that is not covered by other test files:
 * - ExceptionPage.content() throws when no exception in attributes
 * - Page.middlewareProcessBefore(request) new parameter signature
 * - DynamicPage can still be subclassed (simplified to just extend Page)
 * - RequestDTO.cookies uses case-insensitive header lookup (this["Cookie"])
 * - Router instance-level exceptionPage/nullPage isolation between router instances
 * - Bootstrap.tryHandleSpecialRoute priority ordering without ClientHandler
 */
class PRChangesCoverageTests {

    // ---- ExceptionPage factory changes ----

    @Test
    fun exception_page_content_throws_when_no_exception_in_attributes() {
        val page =
            exceptionPage { _, _, ex ->
                buildResponse<String> {
                    status = 500
                    statusText = "Error"
                    body = ex.message ?: "unknown"
                }
            }

        val req = buildRequest { target = "/" }
        // attributes["exception"] not set → should throw IllegalStateException
        try {
            page.content(req, emptyMap())
            fail("Expected an error when exception attribute is missing")
        } catch (e: IllegalStateException) {
            assertTrue(
                e.message?.contains("exception") == true,
                "Expected error message about missing exception attribute",
            )
        }
    }

    @Test
    fun exception_page_content_extracts_exception_from_request_attributes() {
        val page =
            exceptionPage { _, _, ex ->
                buildResponse<String> {
                    status = 500
                    statusText = "Error"
                    body = ex.message ?: "no message"
                }
            }

        val req = buildRequest { target = "/fail" }
        req.attributes["exception"] = RuntimeException("expected-message")

        val resp = page.content(req, emptyMap())
        assertEquals(500, resp.status)
        assertTrue(
            (resp.body as io.voidx.dto.ResponseBody.StringBody).body.contains("expected-message"),
        )
    }

    @Test
    fun exception_page_content_receives_queries_parameter() {
        var receivedQueries: Map<String, String>? = null

        val page =
            exceptionPage { _, queries, _ ->
                receivedQueries = queries
                buildResponse<String> { status = 500; statusText = "Error"; body = "" }
            }

        val req = buildRequest { target = "/" }
        req.attributes["exception"] = RuntimeException("err")
        page.content(req, mapOf("debug" to "true"))

        assertEquals(mapOf("debug" to "true"), receivedQueries)
    }

    // ---- Page.middlewareProcessBefore new signature ----

    @Test
    fun page_middleware_process_before_passes_request_to_relay() {
        var capturedTarget: String? = null

        val page =
            route("/test") {
                GET { _, _ -> ok("ok") }
            }

        page.before(
            relayBefore { result ->
                capturedTarget = result.getOrNull()?.target
                null
            },
        )

        val req = buildRequest { target = "/test" }
        val resp = page.middlewareProcessBefore(req)

        assertNull(resp, "No short-circuit expected from pass-through middleware")
        assertEquals("/test", capturedTarget)
    }

    @Test
    fun page_middleware_process_before_sets_request_on_returned_response() {
        val page =
            route("/test") {
                GET { _, _ -> ok("ok") }
            }

        page.before(
            relayBefore { _ ->
                buildResponse<String> {
                    status = 401
                    statusText = "Unauthorized"
                    body = "denied"
                }
            },
        )

        val req = buildRequest { target = "/test" }
        val resp = page.middlewareProcessBefore(req)

        assertNotNull(resp)
        assertEquals(401, resp.status)
        // _request should be set to the passed request
        assertEquals(req, resp._request)
    }

    // ---- DynamicPage simplification ----

    @Test
    fun dynamic_page_can_be_subclassed_without_data_field() {
        // DynamicPage now just extends Page(target) with no _data field
        val dynPage =
            object : DynamicPage("/items/{id}") {
                override fun content(
                    request: RequestDTO,
                    queries: Map<String, String>,
                ): ResponseDTO =
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        body = queries["id"] ?: "no-id"
                    }
            }

        assertEquals("/items/{id}", dynPage.target)

        val req = buildRequest { target = "/items/42" }
        val resp = dynPage.content(req, mapOf("id" to "42"))
        assertEquals(200, resp.status)
        assertEquals("42", (resp.body as io.voidx.dto.ResponseBody.StringBody).body)
    }

    // ---- RequestDTO.cookies case-insensitive lookup ----

    @Test
    fun cookies_parsed_correctly_from_cookie_header() {
        val req =
            RequestDTO(
                method = Method.GET,
                target = "/",
                headers = mapOf("Cookie" to "session=abc123; user=alice"),
                body = "",
            )

        val cookies = req.cookies

        assertEquals("abc123", cookies["session"])
        assertEquals("alice", cookies["user"])
    }

    @Test
    fun cookies_empty_when_no_cookie_header() {
        val req =
            RequestDTO(
                method = Method.GET,
                target = "/",
                headers = emptyMap(),
                body = "",
            )

        val cookies = req.cookies

        assertTrue(cookies.isEmpty(), "Expected empty cookies when no Cookie header")
    }

    @Test
    fun cookies_with_percent_encoded_values() {
        val req =
            RequestDTO(
                method = Method.GET,
                target = "/",
                headers = mapOf("Cookie" to "lang=%61%62%63"),
                body = "",
            )

        val cookies = req.cookies

        // %61%62%63 decodes to "abc"
        assertEquals("abc", cookies["lang"])
    }

    // ---- Router instance isolation ----

    @Test
    fun two_router_instances_have_independent_null_pages() {
        val r1 = router { }
        val r2 = router { }

        r1.nullPage =
            notFoundPage { _, _ ->
                buildResponse<String> {
                    status = 404
                    statusText = "Not Found"
                    body = "router1-404"
                }
            }

        val req = buildRequest { target = "/missing" }
        val resp1 = r1.nullPage.content(req, emptyMap())
        val resp2 = r2.nullPage.content(req, emptyMap())

        assertEquals("router1-404", (resp1.body as io.voidx.dto.ResponseBody.StringBody).body)
        // r2 should use default 404, not router1's custom one
        assertTrue(
            (resp2.body as io.voidx.dto.ResponseBody.StringBody).body != "router1-404",
            "Router instances should have independent null pages",
        )
    }

    @Test
    fun two_router_instances_have_independent_exception_pages() {
        val r1 = router { }
        val r2 = router { }

        r1.exceptionPage =
            exceptionPage { _, _, _ ->
                buildResponse<String> {
                    status = 500
                    statusText = "Server Error"
                    body = "router1-error"
                }
            }

        val req =
            buildRequest { target = "/" }.also {
                it.attributes["exception"] = RuntimeException("test")
            }
        val resp1 = r1.exceptionPage.content(req, emptyMap())
        val resp2 = r2.exceptionPage.content(req, emptyMap())

        assertEquals("router1-error", (resp1.body as io.voidx.dto.ResponseBody.StringBody).body)
        // r2 should use default exception page, not router1's custom one
        assertTrue(
            (resp2.body as io.voidx.dto.ResponseBody.StringBody).body != "router1-error",
            "Router instances should have independent exception pages",
        )
    }

    // ---- ExceptionPage receives exception type in handler ----

    @Test
    fun exception_page_handler_receives_correct_exception_type() {
        val r = router { }
        var capturedExceptionClass: String? = null

        r.exceptionPage =
            exceptionPage { _, _, ex ->
                capturedExceptionClass = ex::class.simpleName
                buildResponse<String> {
                    status = 500
                    statusText = "Error"
                    body = "err"
                }
            }

        r.addRoute(
            route("/boom") {
                GET { _, _ ->
                    throw IllegalArgumentException("bad arg")
                }
            },
        )

        val sock = SockPR("GET /boom HTTP/1.1\r\nHost: localhost\r\n\r\n")
        sock.handle(1.1, r)

        assertEquals("IllegalArgumentException", capturedExceptionClass)
    }

    // ---- Page.middlewareProcessAfter unchanged behavior ----

    @Test
    fun page_middleware_process_after_calls_relay_after() {
        val page =
            route("/test") {
                GET { _, _ -> ok("ok") }
            }

        var afterCalledWith: Result<ResponseDTO>? = null
        page.after(relayAfter { resp -> afterCalledWith = resp })

        val response = ok("test-response")
        page.middlewareProcessAfter(Result.success(response))

        assertNotNull(afterCalledWith)
        assertEquals(response, afterCalledWith.getOrNull())
    }

    // Additional regression test: verify router.route() DSL creates page via tree
    @Test
    fun router_route_dsl_finds_existing_page_via_tree() {
        val r = router { }
        var callCount = 0
        r.route("/api") {
            GET { _, _ ->
                callCount++
                ok("first")
            }
        }
        r.route("/api") {
            // Adding a handler on same path via DSL should reuse existing handler
            POST { _, _ ->
                callCount++
                ok("post")
            }
        }

        val sock1 = SockPR("GET /api HTTP/1.1\r\nHost: localhost\r\n\r\n")
        sock1.handle(1.1, r)
        val sock2 = SockPR("POST /api HTTP/1.1\r\nHost: localhost\r\n\r\n")
        sock2.handle(1.1, r)

        assertEquals(2, callCount, "Both GET and POST handlers should have been called")
    }
}