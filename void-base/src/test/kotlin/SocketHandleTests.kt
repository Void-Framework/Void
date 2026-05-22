package test

import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.handle
import io.voidx.page.route
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class InMemorySocket(
    private val incoming: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(incoming.toByteArray())
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

class SocketHandleTests {
    @Test
    fun socket_handle_end_to_end_writes_response_and_closes_socket() {
        val r = router { }
        r.addRoute(
            route("/ping") {
                GET { _, _ ->
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "pong"
                    }
                }
            },
        )

        val rawRequest = (
            "GET /ping HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "\r\n"
        )

        val sock = InMemorySocket(rawRequest)
        val srv = Server(r, 1.1)

        // Exercise the extension function which creates a ClientHandler and processes the request
        sock.handle(srv, r)

        val raw = sock.outputString()
        assertTrue(raw.startsWith("HTTP/1.1 200 OK\n"), raw)
        assertTrue(raw.contains("Content-Type: text/plain\n"), raw)
        // Body after header separator
        assertEquals("pong", raw.substringAfter("\n\n"))
        // ClientHandler.start closes the socket in finally
        assertTrue(sock.wasClosed())
    }
}
