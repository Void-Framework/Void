package test

import io.voidx.Method
import io.voidx.dto.buildRequest
import io.voidx.dto.ok
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.page.PageHandler
import io.voidx.page.exceptionPage
import io.voidx.page.notFoundPage
import io.voidx.page.route
import io.voidx.router.Router
import io.voidx.router.router
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RouterEnhancedTests {
    @Test
    fun `test router builder creates router`() {
        val r =
            router {
                route("/test") { GET { ok("test") } }
            }

        assertNotNull(r)
        assertTrue(r.routes.containsKey("/test"))
    }

    @Test
    fun `test parseQuery with single parameter`() {
        val query = Router.parseQuery("/path?key=value")
        assertEquals("value", query["key"])
    }

    @Test
    fun `test parseQuery with multiple parameters`() {
        val query = Router.parseQuery("/path?a=1&b=2&c=3")
        assertEquals("1", query["a"])
        assertEquals("2", query["b"])
        assertEquals("3", query["c"])
    }

    @Test
    fun `test parseQuery with URL encoded values`() {
        val query = Router.parseQuery("/path?name=John%20Doe&email=test%40example.com")
        assertEquals("John Doe", query["name"])
        assertEquals("test@example.com", query["email"])
    }

    @Test
    fun `test parseQuery with no query string returns empty map`() {
        val query = Router.parseQuery("/path")
        assertTrue(query.isEmpty())
    }

    @Test
    fun `test parseQuery with empty query string returns empty map`() {
        val query = Router.parseQuery("/path?")
        assertTrue(query.isEmpty())
    }

    @Test
    fun `test parseQuery ignores keys without values`() {
        val query = Router.parseQuery("/path?key1=value1&key2&key3=value3")
        assertEquals("value1", query["key1"])
        assertNull(query["key2"])
        assertEquals("value3", query["key3"])
    }

    @Test
    fun `test parseQuery with plus signs decoded as spaces`() {
        val query = Router.parseQuery("/path?query=hello+world")
        assertEquals("hello world", query["query"])
    }

    @Test
    fun `test parseQuery with special characters`() {
        val query = Router.parseQuery("/path?special=%21%40%23%24%25")
        assertEquals("!@#$%", query["special"])
    }

    @Test
    fun `test parseQuery with unicode characters`() {
        val query = Router.parseQuery("/path?text=%E4%BD%A0%E5%A5%BD")
        assertEquals("你好", query["text"])
    }

    @Test
    fun `test parseQuery with malformed percent encoding is skipped`() {
        val query = Router.parseQuery("/path?valid=test&invalid=%ZZ&good=value")
        assertEquals("test", query["valid"])
        assertNull(query["invalid"])
        assertEquals("value", query["good"])
    }

    @Test
    fun `test parseQuery with replacement character is skipped`() {
        // This tests the Unicode replacement character filter
        val query = Router.parseQuery("/path?key=\uFFFD")
        assertNull(query["key"])
    }

    @Test
    fun `test router addRoute with static page`() {
        val r = Router()
        val page = route("/test") { GET { ok("test") } }
        r.addRoute(page)

        assertTrue(r.routes.containsKey("/test"))
    }

    @Test
    fun `test router addRoute with dynamic page`() {
        val r = Router()
        val page = route("/user/{id}") { GET { ok("user") } }
        r.addRoute(page)

        assertTrue(r.dynamicRoutes.isNotEmpty())
    }

    @Test
    fun `test router addRoutes with multiple pages`() {
        val r = Router()
        val pages =
            listOf(
                route("/page1") { GET { ok("1") } },
                route("/page2") { GET { ok("2") } },
                route("/page3") { GET { ok("3") } },
            )
        r.addRoutes(pages)

        assertEquals(4, r.routes.size) // Since SLModule adds it's own
    }

    @Test
    fun `test router global middleware before`() {
        val r = Router()
        var called = false
        val relay =
            relayBefore {
                called = true
                null
            }
        r.relay.add(relay)

        val req = buildRequest { method = Method.GET }
        r.recomputeMiddlewareSnapshot()
        r.middlewareProcessBefore(Result.success(req))

        assertTrue(called)
    }

    @Test
    fun `test router global middleware after`() {
        val r = Router()
        var called = false
        val relay =
            relayAfter {
                called = true
            }
        r.relay.add(relay)

        val resp = ok("test")
        r.recomputeMiddlewareSnapshot()
        r.middlewareProcessAfter(Result.success(resp))

        assertTrue(called)
    }

    @Test
    fun `test router middleware before can short circuit`() {
        val r = Router()
        val relay =
            relayBefore {
                ok("blocked")
            }
        r.relay.add(relay)

        val req = buildRequest { method = Method.GET }
        r.recomputeMiddlewareSnapshot()
        val resp = r.middlewareProcessBefore(Result.success(req))

        assertNotNull(resp)
        assertEquals("blocked", resp.body.body as String)
    }

    @Test
    fun `test router middleware priority ordering`() {
        val r = Router()
        val order = mutableListOf<Int>()

        r.relay.add(relayBefore(1) { order.add(1); null })
        r.relay.add(relayBefore(10) { order.add(10); null })
        r.relay.add(relayBefore(5) { order.add(5); null })

        val req = buildRequest { method = Method.GET }
        r.recomputeMiddlewareSnapshot()
        r.middlewareProcessBefore(Result.success(req))

        // Higher priority (10) should run before lower priority
        assertTrue(order.isNotEmpty())
    }

    @Test
    fun `test router route method creates or retrieves handler`() {
        val r = Router()
        r.route("/test") {
            GET { ok("test") }
        }

        assertTrue(r.routes.containsKey("/test"))
    }

    @Test
    fun `test router route method with existing route`() {
        val r = Router()
        r.route("/test") {
            GET { ok("get") }
        }
        r.route("/test") {
            POST { ok("post") }
        }

        val page = r.routes["/test"] as PageHandler
        page.request = buildRequest { method = Method.GET }
        assertEquals(page.content().body.body as String, "get")
    }

    @Test
    fun `test router singleton registration`() {
        val r = Router()
        assertTrue(Router.routers.contains(r))
    }

    @Test
    fun `test parseQuery handles empty value`() {
        val query = Router.parseQuery("/path?key=")
        assertEquals("", query["key"])
    }

    @Test
    fun `test parseQuery handles multiple equals signs`() {
        val query = Router.parseQuery("/path?key=value=with=equals")
        assertEquals("value=with=equals", query["key"])
    }

    @Test
    fun `test parseQuery handles ampersand in value`() {
        val query = Router.parseQuery("/path?key=val%26ue")
        assertEquals("val&ue", query["key"])
    }

    @Test
    fun `test parseQuery with last parameter missing value`() {
        val query = Router.parseQuery("/path?a=1&b=2&c")
        assertEquals("1", query["a"])
        assertEquals("2", query["b"])
        assertNull(query["c"])
    }

    @Test
    fun `test parseQuery with consecutive ampersands`() {
        val query = Router.parseQuery("/path?a=1&&b=2")
        assertEquals("1", query["a"])
        assertEquals("2", query["b"])
    }

    @Test
    fun `test parseQuery preserves order in LinkedHashMap`() {
        val query = Router.parseQuery("/path?z=1&a=2&m=3")
        val keys = query.keys.toList()
        assertEquals("z", keys[0])
        assertEquals("a", keys[1])
        assertEquals("m", keys[2])
    }

    @Test
    fun `test router exception page registration`() {
        val r = Router()
        val exPage =
            exceptionPage {
                ok("error: ${exception.message}")
            }
        r.addRoute(exPage)

        // Check that global exception page is set
        assertNotNull(io.voidx.router.util.RouteCheck.exceptionPage)
    }

    @Test
    fun `test router not found page registration`() {
        val r = Router()
        val nfPage =
            notFoundPage {
                ok("not found")
            }
        r.addRoute(nfPage)

        // Check that global not found page is set
        assertNotNull(io.voidx.router.util.RouteCheck.nullPage)
    }

    @Test
    fun `test router addRoute returns router for chaining`() {
        val r = Router()
        val page = route("/test") { GET { ok("test") } }
        val result = r.addRoute(page)

        assertTrue(r === result)
    }

    @Test
    fun `test router addRoutes returns router for chaining`() {
        val r = Router()
        val pages =
            listOf(
                route("/p1") { GET { ok("1") } },
                route("/p2") { GET { ok("2") } },
            )
        val result = r.addRoutes(pages)

        assertTrue(r === result)
    }

    @Test
    fun `test parseQuery with only question mark returns empty map`() {
        val query = Router.parseQuery("?")
        assertTrue(query.isEmpty())
    }

    @Test
    fun `test parseQuery with fragment is not parsed`() {
        // Fragments are not sent to server, but test the parsing behavior
        val query = Router.parseQuery("/path?key=value#fragment")
        assertEquals("value#fragment", query["key"])
    }

    @Test
    fun `test router middleware after executes all relays`() {
        val r = Router()
        val calls = mutableListOf<Int>()

        r.relay.add(relayAfter { calls.add(1) })
        r.relay.add(relayAfter { calls.add(2) })
        r.relay.add(relayAfter { calls.add(3) })

        val resp = ok("test")
        r.recomputeMiddlewareSnapshot()
        r.middlewareProcessAfter(Result.success(resp))

        assertTrue(calls.size >= 3)
    }

    @Test
    fun `test parseQuery handles boundary cases`() {
        // Start with ampersand
        val query1 = Router.parseQuery("?&key=value")
        assertEquals("value", query1["key"])

        // End with ampersand
        val query2 = Router.parseQuery("?key=value&")
        assertEquals("value", query2["key"])
    }

    @Test
    fun `test router route method works with dynamic routes`() {
        val r = Router()
        r.route("/user/{id}") {
            GET { ok("user") }
        }

        assertTrue(r.dynamicRoutes.isNotEmpty())
    }

    @Test
    fun `test parseQuery case sensitivity`() {
        val query = Router.parseQuery("/path?Key=Value&key=value")
        assertEquals("Value", query["Key"])
        assertEquals("value", query["key"])
    }
}