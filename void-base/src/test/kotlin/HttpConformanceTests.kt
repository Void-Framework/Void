package test

import io.voidx.api.method.Method
import io.voidx.dto.http.buildRequest
import io.voidx.router.Router
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HttpConformanceTests {
    @Test
    fun headers_are_case_insensitive() {
        val req = buildRequest {
            method = Method.GET
            target = "/"
            headers["content-type"] = "text/plain"
            headers["X-Custom-Header"] = "ABC"
        }

        // Exact case
        assertEquals("text/plain", req["content-type"])
        // Different casing should still resolve
        assertEquals("text/plain", req["Content-Type"])
        assertEquals("ABC", req["x-custom-header"])
        // Absent header remains null
        assertNull(req["Does-Not-Exist"])
    }

    @Test
    fun query_string_is_url_decoded() {
        val raw = "/search?q=hello+world&path=%2Fapi%2Fv1%2Bplus&empty=&encoded=%E2%9C%93"
        val q = Router.parseQuery(raw)
        assertEquals("hello world", q["q"])
        assertEquals("/api/v1+plus", q["path"])
        assertEquals("", q["empty"]) // retained as empty string value
        assertEquals("✓", q["encoded"]) // unicode checkmark
    }
}
