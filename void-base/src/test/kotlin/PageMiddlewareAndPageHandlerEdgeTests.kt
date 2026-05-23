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

private class Sock(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class PageMiddlewareAndPageHandlerEdgeTests {
    @Test
    fun page_handler_returns_405_when_method_unhandled() {
        val r = router { }
        r.addRoute(
            route("/only-get") {
                GET { _, _ ->
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "g"
                    }
                }
            },
        )

        // Send PUT which has no handler. PageHandler.content() should return emptyResponse() default
        val sock = Sock("PUT /only-get HTTP/1.1\r\nHost: x\r\nContent-Length: 0\r\n\r\n")
        val srv = Server(r, 1.1)
        sock.handle(1.1, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 405 Method Not Allowed\n"), raw)
        val body = raw.substringAfter("\n\n")
        assertEquals("Method Not Allowed", body)
    }
}
