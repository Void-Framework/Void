package test

import io.voidx.dto.buildResponse
import io.voidx.error
import io.voidx.handle
import io.voidx.middleware.relayBefore
import io.voidx.page.badRequestPage
import io.voidx.page.notFoundPage
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class TestMalformedSocket(
    private val incoming: ByteArray,
) : Socket() {
    constructor(incoming: String) : this(incoming.toByteArray())

    private val inBytes = ByteArrayInputStream(incoming)
    private val outBytes = ByteArrayOutputStream()
    private var closedFlag = false

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    override fun close() {
        closedFlag = true
    }

    fun outputString(): String = outBytes.toString().replace("\r\n", "\n")

    fun wasClosed(): Boolean = closedFlag
}

/**
 * Tests for the changed Socket.handle() and Socket.error() extension functions.
 *
 * Covers:
 * - Malformed request (empty stream) triggers 400 Bad Request via badRequestPage
 * - Custom router.badRequestPage response is used
 * - Socket.error() middleware short-circuit: when a BEFORE middleware returns a response,
 *   that response is written (not the exception page)
 * - Socket.error() without middleware falls through to the exception page
 * - Socket is always closed after handle()
 */
class SocketHandleMalformedTests {

    @Test
    fun malformed_request_writes_400_response() {
        val r = router { }
        // Empty input triggers Malformed=true attribute
        val sock = TestMalformedSocket(ByteArray(0))

        sock.handle(1.1, r)

        val output = sock.outputString()
        // Default badRequestPage returns 400
        assertTrue(output.contains("400"), "Expected 400 in response but got:\n$output")
    }

    @Test
    fun custom_bad_request_page_is_used_for_malformed_request() {
        val r = router { }
        r.badRequestPage =
            badRequestPage { _, _ ->
                buildResponse<String> {
                    status = 400
                    statusText = "Bad Request"
                    headers["Content-Type"] = "text/plain"
                    body = "custom-bad-request"
                }
            }

        val sock = TestMalformedSocket(ByteArray(0))
        sock.handle(1.1, r)

        val output = sock.outputString()
        assertTrue(output.contains("custom-bad-request"), "Expected custom bad request body but got:\n$output")
        assertTrue(output.contains("400"), "Expected 400 status but got:\n$output")
    }

    @Test
    fun handle_closes_socket_on_malformed_request() {
        val r = router { }
        val sock = TestMalformedSocket(ByteArray(0))

        sock.handle(1.1, r)

        assertTrue(sock.wasClosed(), "Socket should be closed after handle()")
    }

    @Test
    fun handle_closes_socket_on_valid_request() {
        val r = router { }
        val raw = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n"
        val sock = TestMalformedSocket(raw)

        sock.handle(1.1, r)

        assertTrue(sock.wasClosed(), "Socket should always be closed after handle()")
    }

    @Test
    fun socket_error_with_middleware_short_circuit_writes_middleware_response() {
        val r = router { }
        // Add a global BEFORE middleware that returns a custom response for exceptions
        with(r) {
            +relayBefore { result ->
                if (result.isFailure) {
                    buildResponse<String> {
                        status = 503
                        statusText = "Service Unavailable"
                        headers["Content-Type"] = "text/plain"
                        body = "middleware-handled-error"
                    }
                } else {
                    null
                }
            }
        }

        val sock = TestMalformedSocket("")
        sock.error(1.1, r, RuntimeException("test error"))

        val output = sock.outputString()
        assertTrue(output.contains("503"), "Expected 503 from middleware but got:\n$output")
        assertTrue(output.contains("middleware-handled-error"), "Expected middleware body but got:\n$output")
    }

    @Test
    fun socket_error_without_middleware_uses_exception_page() {
        val r = router { }
        // No BEFORE middleware, so falls through to exception page
        val sock = TestMalformedSocket("")
        sock.error(1.1, r, RuntimeException("unexpected"))

        val output = sock.outputString()
        // Default exception page returns 500
        assertTrue(output.contains("500"), "Expected 500 from default exception page but got:\n$output")
    }

    @Test
    fun socket_error_middleware_short_circuit_also_runs_after_middleware() {
        val r = router { }
        var afterCalled = false
        with(r) {
            +relayBefore { result ->
                if (result.isFailure) {
                    buildResponse<String> {
                        status = 503
                        statusText = "Service Unavailable"
                        body = "error-intercepted"
                    }
                } else {
                    null
                }
            }
            +io.voidx.middleware.relayAfter { resp ->
                if (resp.isSuccess) afterCalled = true
            }
        }

        val sock = TestMalformedSocket("")
        sock.error(1.1, r, RuntimeException("oops"))

        assertTrue(afterCalled, "Global AFTER middleware should run even when BEFORE short-circuits")
    }

    @Test
    fun default_not_found_page_is_returned_for_missing_routes() {
        val r = router { }
        r.nullPage =
            notFoundPage { _, _ ->
                buildResponse<String> {
                    status = 404
                    statusText = "Not Found"
                    headers["Content-Type"] = "text/plain"
                    body = "not-found-custom"
                }
            }

        val raw = "GET /nonexistent HTTP/1.1\r\nHost: localhost\r\n\r\n"
        val sock = TestMalformedSocket(raw)
        sock.handle(1.1, r)

        val output = sock.outputString()
        assertTrue(output.contains("404"), "Expected 404 but got:\n$output")
        assertTrue(output.contains("not-found-custom"), "Expected custom 404 body but got:\n$output")
    }
}
