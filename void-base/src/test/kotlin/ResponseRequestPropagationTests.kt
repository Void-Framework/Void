package test

import io.voidx.Server
import io.voidx.dto.ok
import io.voidx.handle
import io.voidx.middleware.relayAfter
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

private class SockRRP(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class ResponseRequestPropagationTests {
    @Test
    fun response_request_is_set_before_after_middleware_runs() {
        val r = router { }
        var seenTarget: String? = null
        with(r) {
            +relayAfter { resp -> seenTarget = resp.getOrNull()?._request?.target }
        }

        r.addRoute(route("/rrp") { GET { _, _ -> ok("ok") } })

        val sock = SockRRP("GET /rrp?x=1 HTTP/1.1\r\nHost: x\r\n\r\n")
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 200 OK\n"), raw)
        assertEquals("/rrp?x=1", seenTarget)
    }
}
