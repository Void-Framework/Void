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

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "http://example.com"
                headers["Access-Control-Request-Method"] = "POST" // required for preflight
            }

        val resp = page.middlewareProcessBefore()
        resp?.let { page.middlewareProcessAfter(resp.toResult()) }
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
                ok("Hello")
            }

        page.request =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "http://example.com"
            }

        val resp = page.middlewareProcessBefore()
        resp?.let { page.middlewareProcessAfter(resp.toResult()) }
        assertNull(resp)
    }

    @Test
    fun `origin whitelist works`() {
        val allowed = setOf("https://allowed.com")
        val page =
            route("/test") {
                corsMiddleware(allowed)
                ok("ok")
            }

        // allowed origin
        page.request =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "https://allowed.com"
            }

        val resp = page.content()
        // allowed origin
        resp._request =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "https://allowed.com"
            }
        resp.let { page.middlewareProcessAfter(resp.toResult()) }
        assertEquals("https://allowed.com", resp.headers["Access-Control-Allow-Origin"])

        val resp2 = page.content()
        // disallowed origin
        resp2._request =
            buildRequest {
                method = Method.GET
                headers["Origin"] = "https://notallowed.com"
            }
        resp2.let { page.middlewareProcessAfter(it.toResult()) }
        assertNull(resp2.headers["Access-Control-Allow-Origin"]) // no header
    }
}
