package test

import io.voidx.Method
import io.voidx.dto.buildRequest
import io.voidx.dto.buildResponse
import io.voidx.dto.ok
import io.voidx.middleware.corsMiddleware
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.router.router
import io.voidx.util.toResult
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CorsMiddlewareTests {
    @Test
    fun `OPTIONS preflight is short circuited`() {
        val page =
            route("/test") {
                corsMiddleware()
            }

        val req =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "http://example.com"
                headers["Access-Control-Request-Method"] = "POST" // required for preflight
            }

        val resp = page.middlewareProcessBefore(req)
        resp?.let { page.middlewareProcessAfter(Result.success(resp)) }
        assertNotNull(resp)
        assertEquals(200, resp?.status)
        assertEquals("", resp?.body?.body as String)
        assertEquals("*", resp.headers["Access-Control-Allow-Origin"])
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", resp.headers["Access-Control-Allow-Methods"])
        assertEquals("*", resp.headers["Access-Control-Allow-Headers"])
        assertNull(resp.headers["Access-Control-Allow-Credentials"])
    }

    @Test
    fun `normal request continues to route`() {
        val page =
            route("/test") {
                corsMiddleware()
                GET { _, _ -> ok("Hello") }
            }

        val req =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "http://example.com"
            }

        val resp = page.middlewareProcessBefore(req)
        resp?.let { page.middlewareProcessAfter(Result.success(resp)) }
        assertNull(resp)
    }

    @Test
    fun `origin whitelist works`() {
        val allowed = setOf("https://allowed.com")
        val page =
            route("/test") {
                corsMiddleware(allowed)
                GET { _, _ -> ok("ok") }
            }

        // allowed origin
        val req1 =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "https://allowed.com"
            }

        val resp = page.content(req1, emptyMap())
        // allowed origin
        resp._request = req1
        page.middlewareProcessAfter(Result.success(resp))
        assertEquals("https://allowed.com", resp.headers["Access-Control-Allow-Origin"])

        // disallowed origin
        val req2 =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "https://notallowed.com"
            }
        val resp2 = page.content(req2, emptyMap())
        resp2._request = req2
        page.middlewareProcessAfter(Result.success(resp2))
        assertNull(resp2.headers["Access-Control-Allow-Origin"]) // no header
    }
}
