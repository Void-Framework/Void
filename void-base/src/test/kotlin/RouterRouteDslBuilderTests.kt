package test

import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.handle
import io.voidx.router.Router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class RouteSock(
    private val req: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(req.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class RouterRouteDslBuilderTests {
    @Test
    fun router_route_builder_creates_and_registers_page_handler() {
        val r = Router()

        // First call should create a new PageHandler and register it
        r.route("/d") {
            GET { _, _ ->
                buildResponse<String> {
                    status = 200
                    statusText = "OK"
                    headers["Content-Type"] = "text/plain"
                    body = "get1"
                }
            }
        }

        // Ensure it was registered
        assertNotNull(r.routes["/d"], "Route should be registered on first builder call")

        // Second call for same path should reuse existing handler (adds POST)
        r.route("/d") {
            POST { _, _ ->
                buildResponse<String> {
                    status = 200
                    statusText = "OK"
                    headers["Content-Type"] = "text/plain"
                    body = "post1"
                }
            }
        }

        val srv = Server(r, 1.1)

        // GET request
        val s1 = RouteSock("GET /d HTTP/1.1\r\nHost: x\r\n\r\n")
        s1.handle(srv, r)
        val t1 = s1.text()
        assertTrue(t1.startsWith("HTTP/1.1 200 OK\n"), t1)
        assertEquals("get1", t1.substringAfter("\n\n"))

        // POST request
        val s2 = RouteSock("POST /d HTTP/1.1\r\nHost: x\r\nContent-Length: 0\r\n\r\n")
        s2.handle(srv, r)
        val t2 = s2.text()
        assertTrue(t2.startsWith("HTTP/1.1 200 OK\n"), t2)
        assertEquals("post1", t2.substringAfter("\n\n"))
    }
}
