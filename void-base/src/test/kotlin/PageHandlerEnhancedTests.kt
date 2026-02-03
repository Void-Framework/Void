package test

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.buildRequest
import io.voidx.dto.ok
import io.voidx.page.PageHandler
import io.voidx.page.route
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PageHandlerEnhancedTests {
    @Test
    fun `test GET handler returns correct response`() {
        val handler =
            route("/api") {
                GET { ok("get response") }
            }

        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("get response", resp.body.body as String)
    }

    @Test
    fun `test POST handler returns correct response`() {
        val handler =
            route("/api") {
                POST { ok("post response") }
            }

        handler.request = buildRequest { method = Method.POST }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("post response", resp.body.body as String)
    }

    @Test
    fun `test PUT handler returns correct response`() {
        val handler =
            route("/api") {
                PUT { ok("put response") }
            }

        handler.request = buildRequest { method = Method.PUT }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("put response", resp.body.body as String)
    }

    @Test
    fun `test DELETE handler returns correct response`() {
        val handler =
            route("/api") {
                DELETE { ok("delete response") }
            }

        handler.request = buildRequest { method = Method.DELETE }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("delete response", resp.body.body as String)
    }

    @Test
    fun `test HEAD handler returns correct response`() {
        val handler =
            route("/api") {
                HEAD { ok("head response") }
            }

        handler.request = buildRequest { method = Method.HEAD }
        val resp = handler.content()

        assertEquals(200, resp.status)
    }

    @Test
    fun `test OPTIONS handler returns correct response`() {
        val handler =
            route("/api") {
                OPTIONS { ok("options response") }
            }

        handler.request = buildRequest { method = Method.OPTIONS }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("options response", resp.body.body as String)
    }

    @Test
    fun `test PATCH handler returns correct response`() {
        val handler =
            route("/api") {
                PATCH { ok("patch response") }
            }

        handler.request = buildRequest { method = Method.PATCH }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("patch response", resp.body.body as String)
    }

    @Test
    fun `test TRACE handler returns correct response`() {
        val handler =
            route("/api") {
                TRACE { ok("trace response") }
            }

        handler.request = buildRequest { method = Method.TRACE }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("trace response", resp.body.body as String)
    }

    @Test
    fun `test CONNECT handler returns correct response`() {
        val handler =
            route("/api") {
                CONNECT { ok("connect response") }
            }

        handler.request = buildRequest { method = Method.CONNECT }
        val resp = handler.content()

        assertEquals(200, resp.status)
        assertEquals("connect response", resp.body.body as String)
    }

    @Test
    fun `test unhandled method returns empty response`() {
        val handler =
            route("/api") {
                GET { ok("get only") }
            }

        handler.request = buildRequest { method = Method.POST }
        val resp = handler.content()

        assertEquals(0, resp.status) // empty response
    }

    @Test
    fun `test multiple handlers can be registered on same route`() {
        val handler =
            route("/api") {
                GET { ok("get") }
                POST { ok("post") }
                PUT { ok("put") }
            }

        handler.request = buildRequest { method = Method.GET }
        assertEquals("get", handler.content().body.body as String)

        handler.request = buildRequest { method = Method.POST }
        assertEquals("post", handler.content().body.body as String)

        handler.request = buildRequest { method = Method.PUT }
        assertEquals("put", handler.content().body.body as String)
    }

    @Test
    fun `test handler can access request in lambda`() {
        val handler =
            route("/api") {
                GET { req ->
                    ok("target: ${req.target}, method: ${req.method}")
                }
            }

        handler.request =
            buildRequest {
                method = Method.GET
                target = "/api"
            }
        val resp = handler.content()

        assertEquals("target: /api, method: GET", resp.body.body as String)
    }

    @Test
    fun `test fluent chaining returns handler`() {
        val handler = route("/api") { }
        val result =
            handler
                .GET { ok("get") }
                .POST { ok("post") }

        assertEquals(handler, result)
    }

    @Test
    fun `test handler preserves target`() {
        val handler = route("/my/path") { GET { ok("test") } }
        assertEquals("/my/path", handler.target)
    }

    @Test
    fun `test last registered handler for method wins`() {
        val handler =
            route("/api") {
                GET { ok("first") }
                GET { ok("second") }
            }

        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()

        assertEquals("second", resp.body.body as String)
    }

    @Test
    fun `test handler can be constructed directly`() {
        val handler = PageHandler("/direct")
        handler GET { ok("response") }

        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()

        assertEquals("response", resp.body.body as String)
    }

    @Test
    fun `test responses map is populated correctly`() {
        val handler =
            route("/api") {
                GET { ok("get") }
                POST { ok("post") }
            }

        assertEquals(2, handler.responses.size)
        assertTrue(handler.responses.containsKey(Method.GET))
        assertTrue(handler.responses.containsKey(Method.POST))
    }

    @Test
    fun `test handler with no methods returns empty response`() {
        val handler = route("/api") {}

        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()

        assertEquals(0, resp.status)
    }

    @Test
    fun `test handler can read request headers`() {
        val handler =
            route("/api") {
                GET { req ->
                    val auth = req["Authorization"]
                    ok("auth: $auth")
                }
            }

        handler.request =
            buildRequest {
                method = Method.GET
                headers["Authorization"] = "Bearer token123"
            }
        val resp = handler.content()

        assertEquals("auth: Bearer token123", resp.body.body as String)
    }

    @Test
    fun `test handler can read request body`() {
        val handler =
            route("/api") {
                POST { req ->
                    ok("received: ${req.body}")
                }
            }

        handler.request =
            buildRequest {
                method = Method.POST
                body = "test data"
            }
        val resp = handler.content()

        assertEquals("received: test data", resp.body.body as String)
    }

    @Test
    fun `test handler supports all HTTP methods`() {
        val handler =
            route("/api") {
                GET { ok("GET") }
                POST { ok("POST") }
                PUT { ok("PUT") }
                DELETE { ok("DELETE") }
                HEAD { ok("HEAD") }
                OPTIONS { ok("OPTIONS") }
                PATCH { ok("PATCH") }
                TRACE { ok("TRACE") }
                CONNECT { ok("CONNECT") }
            }

        assertEquals(9, handler.responses.size)
    }

    @Test
    fun `test infix notation works for all methods`() {
        val handler = route("/api") {}

        handler GET { ok("get") }
        handler POST { ok("post") }
        handler PUT { ok("put") }
        handler DELETE { ok("delete") }
        handler HEAD { ok("head") }
        handler OPTIONS { ok("options") }
        handler PATCH { ok("patch") }
        handler TRACE { ok("trace") }
        handler CONNECT { ok("connect") }

        assertEquals(9, handler.responses.size)
    }

    @Test
    fun `test handler returns same instance after method registration`() {
        val handler = route("/api") {}
        val result1 = handler GET { ok("get") }
        val result2 = handler POST { ok("post") }

        assertTrue(handler === result1)
        assertTrue(handler === result2)
    }
}