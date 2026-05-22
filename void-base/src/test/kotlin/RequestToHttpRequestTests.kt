package test

import io.voidx.Method
import io.voidx.dto.*
import kotlin.test.*

class RequestToHttpRequestTests {
    @Test
    fun toHttpRequest_maps_method_uri_headers_and_get_has_no_body() {
        val req =
            buildRequest {
                method = Method.GET
                target = "/items?q=1"
                headers["Accept"] = "application/json"
                headers["X-Test"] = "abc"
                body = "ignored-body" // GET bodies are unusual; ensure HttpRequest has none
            }

        val http = req.toHttpRequest("http://localhost:9000")

        assertEquals("http://localhost:9000/items?q=1", http.uri().toString())
        assertEquals("GET", http.method())

        val headers = http.headers()
        assertEquals("application/json", headers.firstValue("Accept").orElse(null))
        assertEquals("abc", headers.firstValue("X-Test").orElse(null))

        // For GET requests, the Java HttpRequest should have no body publisher
        assertFalse(http.bodyPublisher().isPresent)
    }

    @Test
    fun toHttpRequest_maps_post_body_and_headers() {
        val req =
            buildRequest {
                method = Method.POST
                target = "/submit"
                headers["Content-Type"] = "text/plain"
                body = "hello world"
            }

        val http = req.toHttpRequest("http://example.com")
        assertEquals("http://example.com/submit", http.uri().toString())
        assertEquals("POST", http.method())

        val headers = http.headers()
        assertEquals("text/plain", headers.firstValue("Content-Type").orElse(null))
        // Body should be present for POST
        kotlin.test.assertTrue(http.bodyPublisher().isPresent)
    }

    @Test
    fun buildRequest_various_methods() {
        Method.values().forEach { m ->
            val req =
                buildRequest {
                    method = m
                    target = "/path"
                }
            assertEquals(m, req.method)
            assertEquals("/path", req.target)
        }
    }

    @Test
    fun buildRequest_headers_dsl() {
        val req =
            buildRequest {
                headers {
                    put("A", "1")
                    put("B", "2")
                }
            }
        assertEquals("1", req.get("A"))
        assertEquals("2", req.get("B"))
        assertEquals("1", req.headers["A"])
        assertNull(req.get("C"))
    }
}
