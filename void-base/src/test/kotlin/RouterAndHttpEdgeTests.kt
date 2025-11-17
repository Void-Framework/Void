package test

import io.voidx.dto.http.noContent
import io.voidx.dto.http.ok
import io.voidx.dto.http.writeHTTP
import io.voidx.html.page.apiRoute
import io.voidx.router.Router
import io.voidx.router.router
import io.voidx.util.HtmlIntegration
import java.io.ByteArrayOutputStream
import kotlin.test.*

class RouterAndHttpEdgeTests {
    @Test
    fun parseQuery_ignores_keys_without_value() {
        val raw = "/q?onlyKey&alsoOnly&k=v"
        val map = Router.parseQuery(raw)
        assertEquals("v", map["k"])
        assertNull(map["onlyKey"]) // ignored
        assertNull(map["alsoOnly"]) // ignored
    }

    @Test
    fun no_content_writes_zero_length_and_no_body() {
        val resp = noContent(mutableMapOf("Content-Type" to "text/plain"))
        val out = ByteArrayOutputStream()
        out.writeHTTP(resp, 1.1)
        val normalized = out.toString().replace("\r\n", "\n")
        assertTrue(normalized.startsWith("HTTP/1.1 204 No Content\n"))
        assertTrue(normalized.contains("Content-Length: 0\n"))
        // Body should be empty after header separator
        val body = normalized.substringAfter("\n\n")
        assertEquals("", body)
    }

    @Test
    fun response_helper_status_shortcuts() {
        val okResp = ok("hi", mutableMapOf("Content-Type" to "text/plain"))
        assertEquals(200, okResp.status)
        assertEquals("OK", okResp.statusText)

        val created =
            io.voidx.dto.http
                .created("x", mutableMapOf())
        assertEquals(201, created.status)
        assertEquals("Created", created.statusText)

        val accepted =
            io.voidx.dto.http
                .accepted("y", mutableMapOf())
        assertEquals(202, accepted.status)
        assertEquals("Accepted", accepted.statusText)
    }
}
