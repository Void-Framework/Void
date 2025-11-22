package test

import io.voidx.ClientHandler
import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.page.exceptionPage
import io.voidx.router.Router
import io.voidx.router.router
import io.voidx.router.util.RouteCheck
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertTrue

private class CapturingSocket : Socket() {
    private val out = ByteArrayOutputStream()
    private var closedFlag = false

    override fun getInputStream(): InputStream = InputStream.nullInputStream()

    override fun getOutputStream(): OutputStream = out

    override fun close() {
        closedFlag = true
    }

    fun bytes(): ByteArray = out.toByteArray()

    fun closed(): Boolean = closedFlag
}

class RouterErrorAndClientHandlerTests {
    @Test
    fun router_error_uses_exception_page_response() {
        val r = Router()
        // Install an ExceptionPage that returns a distinct response
        val ex =
            exceptionPage {
                buildResponse<String> {
                    status = 500
                    statusText = "Internal Server Error"
                    headers["Content-Type"] = "text/plain"
                    body = "boom:${exception.message}"
                }
            }
        RouteCheck.exceptionPage = ex

        val sock = CapturingSocket()
        val srv = Server(r, 1.1)
        val ch = ClientHandler(sock, srv, r)

        r.error(ch, IllegalStateException("X"))

        val raw = String(sock.bytes()).replace("\r\n", "\n")
        assertTrue(raw.startsWith("HTTP/1.1 500 Internal Server Error\n"))
        assertTrue(raw.contains("Content-Type: text/plain\n"))
        assertTrue(raw.substringAfter("\n\n").startsWith("boom:X"))
    }

    @Test
    fun client_handler_error_invokes_router_error_and_closes_socket_when_no_before_short_circuit() {
        val r = router { }
        // Minimal exception page to ensure write happens
        RouteCheck.exceptionPage =
            exceptionPage {
                buildResponse<String> {
                    status = 500
                    statusText = "Internal Server Error"
                    headers["Content-Type"] = "text/plain"
                    body = "err"
                }
            }

        val sock = CapturingSocket()
        val srv = Server(r, 1.1)
        val ch = ClientHandler(sock, srv, r)

        ch.error(RuntimeException("boom"))

        val raw = String(sock.bytes()).replace("\r\n", "\n")
        assertTrue(raw.startsWith("HTTP/1.1 500 Internal Server Error\n"))
        // In the non-short-circuit path, ClientHandler.error should close the socket in finally
        assertTrue(sock.closed())
    }
}
