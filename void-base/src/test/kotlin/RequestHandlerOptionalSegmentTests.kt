package test

import io.voidx.Server
import io.voidx.dto.ok
import io.voidx.handle
import io.voidx.page.DynamicPage
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class SockROS(
    raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class RequestHandlerOptionalSegmentTests {
    @Test
    fun optional_trailing_segment_absent_is_allowed() {
        val r = router { }
        val p =
            object : DynamicPage("/blog/{slug?}") {
                override fun content() = ok(data["slug"] ?: "index")
            }
        r.addRoute(p)
        val srv = Server(r, 1.1)
        val sock = SockROS("GET /blog HTTP/1.1\r\nHost: x\r\n\r\n")
        sock.handle(srv, r)
        val out = sock.text()
        assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
        assertTrue(out.trim().endsWith("index"), out)
    }

    @Test
    fun favicon_is_not_ignored_by_dynamic_matching() {
        val r = router { }
        // A dynamic that would match anything under root if not for favicon bypass
        val p =
            object : DynamicPage("/{x}") {
                override fun content() = ok("dyn")
            }
        r.addRoute(p)

        val srv = Server(r, 1.1)
        val sock = SockROS("GET /favicon.ico HTTP/1.1\r\nHost: x\r\n\r\n")
        sock.handle(srv, r)
        val out = sock.text()
        // Should hit 404 rather than dynamic route
        assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
        assertEquals("dyn", out.substringAfter("\n\n").trim(), out)
    }
}
