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
        val req =
            buildRequest {
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

    @Test
    fun query_string_malformed_percent_encoding_is_skipped() {
        val raw = "/x?ok=1&bad=%ZZ&alsoBad=%E2%9C&fine=hello"
        val q = Router.parseQuery(raw)
        assertEquals("1", q["ok"]) // good pair kept
        // Malformed values should be skipped (not present)
        kotlin.test.assertNull(q["bad"])
        kotlin.test.assertNull(q["alsoBad"])
        assertEquals("hello", q["fine"]) // following pairs still parsed
    }
}
