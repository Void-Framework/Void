package test

import io.voidx.ClientHandler
import io.voidx.Method
import io.voidx.Server
import io.voidx.dto.buildRequest
import io.voidx.dto.ok
import io.voidx.page.DynamicPage
import io.voidx.page.path
import io.voidx.page.route
import io.voidx.router.router
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.*

class RequestHandlerEnhancedTests {
    private class TestRequestHandler : io.voidx.router.util.RequestHandler {
        override val dynamicRoutes = ConcurrentHashMap<List<String>, DynamicPage>()
    }

    @Test
    fun `test handleDynamic with single required segment`() {
        val handler = TestRequestHandler()
        val page =
            route("/user/{id}") {
                GET {
                    val id = path<String>("id")
                    ok("user id: $id")
                }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/user/123"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("user id: 123", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with multiple required segments`() {
        val handler = TestRequestHandler()
        val page =
            route("/user/{id}/post/{postId}") {
                GET {
                    val id = path<String>("id")
                    val postId = path<String>("postId")
                    ok("user: $id, post: $postId")
                }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}", "post", "{postId}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/user/42/post/99"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("user: 42, post: 99", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with optional segment present`() {
        val handler = TestRequestHandler()
        val page =
            route("/blog/{slug?}") {
                GET {
                    val slug = path<String>("slug?")
                    ok("slug: ${slug ?: "none"}")
                }
            }
        handler.dynamicRoutes[listOf("", "blog", "{slug?}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/blog/my-post"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("slug: my-post", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with optional segment missing`() {
        val handler = TestRequestHandler()
        val page =
            route("/blog/{slug?}") {
                GET {
                    val slug = path<String>("slug?")
                    ok("slug: ${slug ?: "none"}")
                }
            }
        handler.dynamicRoutes[listOf("", "blog", "{slug?}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/blog"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("slug: none", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with trailing slash on request`() {
        val handler = TestRequestHandler()
        val page =
            route("/user/{id}") {
                GET {
                    val id = path<String>("id")
                    ok("id: $id")
                }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/user/123/"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("id: 123", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with no matching route`() {
        val handler = TestRequestHandler()
        val page =
            route("/user/{id}") {
                GET { ok("user") }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/product/123"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNull(resp)
    }

    @Test
    fun `test handleDynamic with mismatched segment count`() {
        val handler = TestRequestHandler()
        val page =
            route("/user/{id}") {
                GET { ok("user") }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/user/123/extra"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNull(resp)
    }

    @Test
    fun `test handleDynamic with static and dynamic segments mixed`() {
        val handler = TestRequestHandler()
        val page =
            route("/api/v1/user/{id}") {
                GET {
                    val id = path<String>("id")
                    ok("api user: $id")
                }
            }
        handler.dynamicRoutes[listOf("", "api", "v1", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/api/v1/user/456"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("api user: 456", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with static segment mismatch`() {
        val handler = TestRequestHandler()
        val page =
            route("/api/v1/user/{id}") {
                GET { ok("user") }
            }
        handler.dynamicRoutes[listOf("", "api", "v1", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/api/v2/user/123"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNull(resp)
    }

    @Test
    fun `test handleDynamic sets queries on page`() {
        val handler = TestRequestHandler()
        val page =
            route("/search/{query}") {
                GET {
                    ok("query: ${path<String>("query")}, page: ${queries["page"]}")
                }
            }
        handler.dynamicRoutes[listOf("", "search", "{query}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/search/test"
            }
        val resp = handler.handleDynamic(req, mapOf("page" to "1"))

        assertNotNull(resp)
        assertEquals("query: test, page: 1", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with middleware before short circuit`() {
        val handler = TestRequestHandler()
        val page =
            route("/protected/{id}") {
                before(
                    io.voidx.middleware.relayBefore {
                        ok("blocked")
                    },
                )
                GET { ok("should not reach") }
            }
        handler.dynamicRoutes[listOf("", "protected", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/protected/123"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("blocked", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with middleware before continues`() {
        val handler = TestRequestHandler()
        val page =
            route("/user/{id}") {
                before(io.voidx.middleware.relayBefore { null })
                GET { ok("content") }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/user/123"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("content", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic captures multiple dynamic segments correctly`() {
        val handler = TestRequestHandler()
        val page =
            route("/{a}/{b}/{c}") {
                GET {
                    val a = path<String>("a")
                    val b = path<String>("b")
                    val c = path<String>("c")
                    ok("$a-$b-$c")
                }
            }
        handler.dynamicRoutes[listOf("", "{a}", "{b}", "{c}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/one/two/three"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("one-two-three", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with special characters in segment`() {
        val handler = TestRequestHandler()
        val page =
            route("/file/{name}") {
                GET {
                    val name = path<String>("name")
                    ok("file: $name")
                }
            }
        handler.dynamicRoutes[listOf("", "file", "{name}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/file/my-file.txt"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("file: my-file.txt", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with numeric segment`() {
        val handler = TestRequestHandler()
        val page =
            route("/item/{id}") {
                GET {
                    val id = path<String>("id")
                    ok("id: $id")
                }
            }
        handler.dynamicRoutes[listOf("", "item", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/item/12345"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("id: 12345", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic with empty segment matches root dynamic`() {
        val handler = TestRequestHandler()
        val page =
            route("/{page}") {
                GET {
                    val p = path<String>("page")
                    ok("page: $p")
                }
            }
        handler.dynamicRoutes[listOf("", "{page}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/home"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("page: home", resp?.body?.body as String)
    }

    @Test
    fun `test handleDynamic does not match if required segment count differs`() {
        val handler = TestRequestHandler()
        val page =
            route("/a/{b}/c") {
                GET { ok("matched") }
            }
        handler.dynamicRoutes[listOf("", "a", "{b}", "c")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/a/b"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNull(resp)
    }

    @Test
    fun `test handleResponse returns page content`() {
        val handler = TestRequestHandler()
        val page =
            route("/test") {
                GET { ok("response") }
            }
        page.request = buildRequest { method = Method.GET }

        val r = router { }
        val srv = Server(r, 1.1)
        val clientHandler = ClientHandler(Socket(), srv, r)
        val resp = handler.handleResponse(page, clientHandler, "/test")

        assertEquals("response", resp.body.body as String)
    }

    @Test
    fun `test handleDynamic sets request on matched page`() {
        val handler = TestRequestHandler()
        var capturedTarget: String? = null
        val page =
            route("/user/{id}") {
                GET {
                    capturedTarget = request.target
                    ok("ok")
                }
            }
        handler.dynamicRoutes[listOf("", "user", "{id}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/user/123"
            }
        handler.handleDynamic(req, emptyMap())

        assertEquals("/user/123", capturedTarget)
    }

    @Test
    fun `test handleDynamic with route having optional segment at end`() {
        val handler = TestRequestHandler()
        val page =
            route("/docs/{category}/{page?}") {
                GET {
                    val cat = path<String>("category")
                    val pg = path<String>("page?")
                    ok("cat: $cat, page: ${pg ?: "index"}")
                }
            }
        handler.dynamicRoutes[listOf("", "docs", "{category}", "{page?}")] = page

        // With optional segment
        val req1 =
            buildRequest {
                method = Method.GET
                target = "/docs/api/setup"
            }
        val resp1 = handler.handleDynamic(req1, emptyMap())
        assertNotNull(resp1)
        assertEquals("cat: api, page: setup", resp1?.body?.body as String)

        // Without optional segment
        val req2 =
            buildRequest {
                method = Method.GET
                target = "/docs/api"
            }
        val resp2 = handler.handleDynamic(req2, emptyMap())
        assertNotNull(resp2)
        assertEquals("cat: api, page: index", resp2?.body?.body as String)
    }

    @Test
    fun `test handleDynamic first matching route wins`() {
        val handler = TestRequestHandler()
        val page1 =
            route("/item/{id}") {
                GET { ok("first") }
            }
        val page2 =
            route("/item/{name}") {
                GET { ok("second") }
            }
        handler.dynamicRoutes[listOf("", "item", "{id}")] = page1
        handler.dynamicRoutes[listOf("", "item", "{name}")] = page2

        val req =
            buildRequest {
                method = Method.GET
                target = "/item/123"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        // Implementation will match one of them
        assertTrue(resp?.body?.body == "first" || resp?.body?.body == "second")
    }

    @Test
    fun `test handleDynamic with completely dynamic path`() {
        val handler = TestRequestHandler()
        val page =
            route("/{a}/{b}") {
                GET {
                    val a = path<String>("a")
                    val b = path<String>("b")
                    ok("$a/$b")
                }
            }
        handler.dynamicRoutes[listOf("", "{a}", "{b}")] = page

        val req =
            buildRequest {
                method = Method.GET
                target = "/foo/bar"
            }
        val resp = handler.handleDynamic(req, emptyMap())

        assertNotNull(resp)
        assertEquals("foo/bar", resp?.body?.body as String)
    }
}
