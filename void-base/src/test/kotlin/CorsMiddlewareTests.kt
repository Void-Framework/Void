package test

import io.voidx.Method
import io.voidx.dto.buildRequest
import io.voidx.dto.ok
import io.voidx.middleware.corsMiddleware
import io.voidx.page.route
import io.voidx.util.toResult
import org.junit.jupiter.api.Assertions.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

    @Test
    fun `preflight with allowed origin includes credentials`() {
        val allowed = setOf("https://trusted.com")
        val page =
            route("/test") {
                corsMiddleware(allowed)
            }

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "https://trusted.com"
                headers["Access-Control-Request-Method"] = "POST"
            }

        val resp = page.middlewareProcessBefore()
        assertNotNull(resp)
        assertEquals(200, resp?.status)
        assertEquals("https://trusted.com", resp?.headers["Access-Control-Allow-Origin"])
        assertEquals("true", resp?.headers["Access-Control-Allow-Credentials"])
    }

    @Test
    fun `preflight with wildcard origin does not include credentials`() {
        val page =
            route("/test") {
                corsMiddleware(null) // wildcard
            }

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "http://any.com"
                headers["Access-Control-Request-Method"] = "POST"
            }

        val resp = page.middlewareProcessBefore()
        assertNotNull(resp)
        assertEquals("*", resp?.headers["Access-Control-Allow-Origin"])
        assertNull(resp?.headers["Access-Control-Allow-Credentials"])
    }

    @Test
    fun `preflight without origin header is rejected`() {
        val page =
            route("/test") {
                corsMiddleware()
            }

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Access-Control-Request-Method"] = "POST"
                // No Origin header
            }

        val resp = page.middlewareProcessBefore()
        assertNull(resp) // should not short-circuit
    }

    @Test
    fun `preflight without request method is rejected`() {
        val page =
            route("/test") {
                corsMiddleware()
            }

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "http://example.com"
                // No Access-Control-Request-Method
            }

        val resp = page.middlewareProcessBefore()
        assertNull(resp) // should not short-circuit
    }

    @Test
    fun `preflight with disallowed origin returns no CORS headers`() {
        val allowed = setOf("https://only-this.com")
        val page =
            route("/test") {
                corsMiddleware(allowed)
            }

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "https://not-this.com"
                headers["Access-Control-Request-Method"] = "POST"
            }

        val resp = page.middlewareProcessBefore()
        assertNotNull(resp)
        assertEquals(200, resp?.status)
        assertNull(resp?.headers["Access-Control-Allow-Origin"])
        assertNull(resp?.headers["Access-Control-Allow-Methods"])
    }

    @Test
    fun `normal request without origin does not add CORS headers`() {
        val page =
            route("/test") {
                corsMiddleware()
                ok("test")
            }

        page.request =
            buildRequest {
                method = Method.GET
                // No Origin header
            }

        val resp = page.content()
        resp._request = page.request
        page.middlewareProcessAfter(resp.toResult())
        assertNull(resp.headers["Access-Control-Allow-Origin"])
    }

    @Test
    fun `after middleware adds CORS headers for allowed origin`() {
        val allowed = setOf("https://example.com")
        val page =
            route("/test") {
                corsMiddleware(allowed)
                ok("content")
            }

        page.request =
            buildRequest {
                method = Method.POST
                headers["Origin"] = "https://example.com"
            }

        val resp = page.content()
        resp._request = page.request
        page.middlewareProcessAfter(resp.toResult())
        assertEquals("https://example.com", resp.headers["Access-Control-Allow-Origin"])
        assertEquals("GET, POST, PUT, DELETE, OPTIONS", resp.headers["Access-Control-Allow-Methods"])
        assertEquals("*", resp.headers["Access-Control-Allow-Headers"])
        assertEquals("true", resp.headers["Access-Control-Allow-Credentials"])
    }

    @Test
    fun `after middleware with wildcard adds wildcard without credentials`() {
        val page =
            route("/test") {
                corsMiddleware()
                ok("content")
            }

        page.request =
            buildRequest {
                method = Method.POST
                headers["Origin"] = "http://any-origin.com"
            }

        val resp = page.content()
        resp._request = page.request
        page.middlewareProcessAfter(resp.toResult())
        assertEquals("*", resp.headers["Access-Control-Allow-Origin"])
        assertNull(resp.headers["Access-Control-Allow-Credentials"])
    }

    @Test
    fun `OPTIONS without Access-Control-Request-Method is not preflight`() {
        val page =
            route("/test") {
                corsMiddleware()
                OPTIONS { ok("options response") }
            }

        page.request =
            buildRequest {
                method = Method.OPTIONS
                headers["Origin"] = "http://example.com"
                // Missing Access-Control-Request-Method makes this not a preflight
            }

        val resp = page.middlewareProcessBefore()
        assertNull(resp) // should not intercept, proceed to handler
    }
}
