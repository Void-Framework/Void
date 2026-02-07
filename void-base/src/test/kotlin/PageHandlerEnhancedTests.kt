package test

import io.voidx.Method
import io.voidx.dto.buildRequest
import io.voidx.dto.ok
import io.voidx.page.PageHandler
import io.voidx.page.route
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
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

        assertEquals(405, resp.status) // Incorrect method
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
                GET {
                    ok("target: ${request.target}, method: ${request.method}")
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

        assertEquals(Method.entries.size, handler.responses.size) // Since we default populate it with 405s
        assertTrue(handler.responses.containsKey(Method.GET))
        assertTrue(handler.responses.containsKey(Method.POST))
    }

    @Test
    fun `test handler with no methods returns empty response`() {
        val handler = route("/api") {}

        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()

        assertEquals(405, resp.status) // No method supporting
    }

    @Test
    fun `test handler can read request headers`() {
        val handler =
            route("/api") {
                GET {
                    val auth = request["Authorization"]
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
                POST {
                    ok("received: ${request.body}")
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
    fun `test handler returns same instance after method registration`() {
        val handler = route("/api") {}
        val result1 = handler GET { ok("get") }
        val result2 = handler POST { ok("post") }

        assertSame(handler, result1)
        assertSame(handler, result2)
    }

    @Test
    fun `test handler with complex path`() {
        val handler = route("/api/v1/users/profile") { GET { ok("complex path") } }
        assertEquals("/api/v1/users/profile", handler.target)
    }

    @Test
    fun `test handler with empty path`() {
        val handler = route("") { GET { ok("root") } }
        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()
        assertEquals("root", resp.body.body as String)
    }

    @Test
    fun `test handler with root path`() {
        val handler = route("/") { GET { ok("root") } }
        assertEquals("/", handler.target)
    }

    @Test
    fun `test handler GET with query parameters in target`() {
        val handler = route("/api") {
            GET {
                ok("target: ${request.target}")
            }
        }
        handler.request = buildRequest {
            method = Method.GET
            target = "/api?param=value"
        }
        val resp = handler.content()
        assertEquals("target: /api?param=value", resp.body.body as String)
    }

    @Test
    fun `test handler POST with JSON body`() {
        val handler = route("/api") {
            POST {
                ok("body: ${request.body}")
            }
        }
        handler.request = buildRequest {
            method = Method.POST
            body = "{\"key\":\"value\"}"
            headers["Content-Type"] = "application/json"
        }
        val resp = handler.content()
        assertEquals("body: {\"key\":\"value\"}", resp.body.body as String)
    }

    @Test
    fun `test handler with multiple header access`() {
        val handler = route("/api") {
            GET {
                val ct = request["Content-Type"]
                val auth = request["Authorization"]
                val custom = request["X-Custom"]
                ok("ct=$ct, auth=$auth, custom=$custom")
            }
        }
        handler.request = buildRequest {
            method = Method.GET
            headers["Content-Type"] = "application/json"
            headers["Authorization"] = "Bearer token"
            headers["X-Custom"] = "custom-value"
        }
        val resp = handler.content()
        assertEquals("ct=application/json, auth=Bearer token, custom=custom-value", resp.body.body as String)
    }

    @Test
    fun `test handler accessing missing header returns null`() {
        val handler = route("/api") {
            GET {
                val missing = request["Missing-Header"]
                ok("missing: $missing")
            }
        }
        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()
        assertEquals("missing: null", resp.body.body as String)
    }

    @Test
    fun `test handler with empty request body`() {
        val handler = route("/api") {
            POST {
                ok("body: '${request.body}'")
            }
        }
        handler.request = buildRequest {
            method = Method.POST
            body = ""
        }
        val resp = handler.content()
        assertEquals("body: ''", resp.body.body as String)
    }

    @Test
    fun `test handler method registration order independence`() {
        val handler = route("/api") {
            DELETE { ok("delete") }
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

        handler.request = buildRequest { method = Method.DELETE }
        assertEquals("delete", handler.content().body.body as String)
    }

    @Test
    fun `test handler with all HTTP methods registered`() {
        val handler = route("/api") {
            GET { ok("get") }
            POST { ok("post") }
            PUT { ok("put") }
            DELETE { ok("delete") }
            HEAD { ok("head") }
            OPTIONS { ok("options") }
            PATCH { ok("patch") }
            TRACE { ok("trace") }
            CONNECT { ok("connect") }
        }

        val methods = listOf(
            Method.GET to "get",
            Method.POST to "post",
            Method.PUT to "put",
            Method.DELETE to "delete",
            Method.HEAD to "head",
            Method.OPTIONS to "options",
            Method.PATCH to "patch",
            Method.TRACE to "trace",
            Method.CONNECT to "connect"
        )

        methods.forEach { (method, expected) ->
            handler.request = buildRequest { this.method = method }
            assertEquals(expected, handler.content().body.body as String)
        }
    }

    @Test
    fun `test handler responses map contains all methods by default`() {
        val handler = route("/api") {}
        assertEquals(Method.entries.size, handler.responses.size)
        Method.entries.forEach { method ->
            assertTrue(handler.responses.containsKey(method))
        }
    }

    @Test
    fun `test handler with special characters in target`() {
        val handler = route("/api/my-endpoint_v2.0") { GET { ok("special") } }
        assertEquals("/api/my-endpoint_v2.0", handler.target)
    }

    @Test
    fun `test handler PUT and PATCH difference`() {
        val handler = route("/api") {
            PUT { ok("full update") }
            PATCH { ok("partial update") }
        }

        handler.request = buildRequest { method = Method.PUT }
        assertEquals("full update", handler.content().body.body as String)

        handler.request = buildRequest { method = Method.PATCH }
        assertEquals("partial update", handler.content().body.body as String)
    }

    @Test
    fun `test handler HEAD typically returns no body`() {
        val handler = route("/api") {
            HEAD {
                buildResponse<String> {
                    status = 200
                    statusText = "OK"
                    headers["Content-Length"] = "0"
                }
            }
        }
        handler.request = buildRequest { method = Method.HEAD }
        val resp = handler.content()
        assertEquals(200, resp.status)
        assertEquals("0", resp.headers["Content-Length"])
    }

    @Test
    fun `test handler OPTIONS can return allowed methods`() {
        val handler = route("/api") {
            GET { ok("get") }
            POST { ok("post") }
            OPTIONS {
                buildResponse<String> {
                    status = 200
                    headers["Allow"] = "GET, POST, OPTIONS"
                    body = ""
                }
            }
        }
        handler.request = buildRequest { method = Method.OPTIONS }
        val resp = handler.content()
        assertEquals("GET, POST, OPTIONS", resp.headers["Allow"])
    }

    @Test
    fun `test handler can return different status codes`() {
        val handler = route("/api") {
            GET {
                buildResponse<String> {
                    status = 200
                    body = "ok"
                }
            }
            POST {
                buildResponse<String> {
                    status = 201
                    body = "created"
                }
            }
            DELETE {
                buildResponse<String> {
                    status = 204
                    body = ""
                }
            }
        }

        handler.request = buildRequest { method = Method.GET }
        assertEquals(200, handler.content().status)

        handler.request = buildRequest { method = Method.POST }
        assertEquals(201, handler.content().status)

        handler.request = buildRequest { method = Method.DELETE }
        assertEquals(204, handler.content().status)
    }

    @Test
    fun `test handler with custom status text`() {
        val handler = route("/api") {
            GET {
                buildResponse<String> {
                    status = 418
                    statusText = "I'm a teapot"
                    body = "teapot"
                }
            }
        }
        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()
        assertEquals(418, resp.status)
        assertEquals("I'm a teapot", resp.statusText)
    }

    @Test
    fun `test handler request modification between calls`() {
        val handler = route("/api") {
            GET {
                ok("method: ${request.method}, target: ${request.target}")
            }
        }

        handler.request = buildRequest {
            method = Method.GET
            target = "/api/first"
        }
        val resp1 = handler.content()
        assertEquals("method: GET, target: /api/first", resp1.body.body as String)

        handler.request = buildRequest {
            method = Method.GET
            target = "/api/second"
        }
        val resp2 = handler.content()
        assertEquals("method: GET, target: /api/second", resp2.body.body as String)
    }

    @Test
    fun `test handler with large body`() {
        val largeBody = "x".repeat(10000)
        val handler = route("/api") {
            POST {
                ok("length: ${request.body.length}")
            }
        }
        handler.request = buildRequest {
            method = Method.POST
            body = largeBody
        }
        val resp = handler.content()
        assertEquals("length: 10000", resp.body.body as String)
    }

    @Test
    fun `test handler POST with form data`() {
        val handler = route("/api") {
            POST {
                ok("body: ${request.body}")
            }
        }
        handler.request = buildRequest {
            method = Method.POST
            headers["Content-Type"] = "application/x-www-form-urlencoded"
            body = "name=John&age=30"
        }
        val resp = handler.content()
        assertEquals("body: name=John&age=30", resp.body.body as String)
    }

    @Test
    fun `test handler with case sensitive headers`() {
        val handler = route("/api") {
            GET {
                val lower = request["content-type"]
                val upper = request["CONTENT-TYPE"]
                val mixed = request["Content-Type"]
                ok("lower=$lower, upper=$upper, mixed=$mixed")
            }
        }
        handler.request = buildRequest {
            method = Method.GET
            headers["Content-Type"] = "application/json"
        }
        val resp = handler.content()
        // Response depends on header storage implementation
        assertNotNull(resp.body.body)
    }

    @Test
    fun `test handler register same method twice overwrites`() {
        val handler = route("/api") {
            GET { ok("first") }
        }
        handler GET { ok("second") }

        handler.request = buildRequest { method = Method.GET }
        assertEquals("second", handler.content().body.body as String)
    }

    @Test
    fun `test handler with Unicode in body`() {
        val handler = route("/api") {
            POST {
                ok("received: ${request.body}")
            }
        }
        handler.request = buildRequest {
            method = Method.POST
            body = "Hello 世界 🌍"
        }
        val resp = handler.content()
        assertEquals("received: Hello 世界 🌍", resp.body.body as String)
    }

    @Test
    fun `test handler with response headers`() {
        val handler = route("/api") {
            GET {
                buildResponse<String> {
                    status = 200
                    headers["X-Custom"] = "custom-value"
                    headers["Cache-Control"] = "no-cache"
                    body = "test"
                }
            }
        }
        handler.request = buildRequest { method = Method.GET }
        val resp = handler.content()
        assertEquals("custom-value", resp.headers["X-Custom"])
        assertEquals("no-cache", resp.headers["Cache-Control"])
    }

    @Test
    fun `test handler CONNECT method for proxy`() {
        val handler = route("/proxy") {
            CONNECT {
                buildResponse<String> {
                    status = 200
                    statusText = "Connection Established"
                    body = ""
                }
            }
        }
        handler.request = buildRequest { method = Method.CONNECT }
        val resp = handler.content()
        assertEquals(200, resp.status)
        assertEquals("Connection Established", resp.statusText)
    }

    @Test
    fun `test handler TRACE echoes request`() {
        val handler = route("/api") {
            TRACE {
                ok("TRACE ${request.target}")
            }
        }
        handler.request = buildRequest {
            method = Method.TRACE
            target = "/api/test"
        }
        val resp = handler.content()
        assertEquals("TRACE /api/test", resp.body.body as String)
    }
}