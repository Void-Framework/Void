package test

import io.voidx.Method
import io.voidx.dto.*
import io.voidx.middleware.RelayBefore
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.page.Page
import io.voidx.page.exceptionPage
import io.voidx.page.notFoundPage
import io.voidx.util.toResult
import kotlin.reflect.KClass
import kotlin.test.*

class PageEnhancedTests {
    private class TestPage : Page("/test") {
        override fun content(request: RequestDTO, queries: Map<String, String>): ResponseDTO =
            buildResponse<String> {
                status = 200
                statusText = "OK"
                body = "test page"
            }
    }

    @Test
    fun `test page attributes can store and retrieve values`() {
        val page = TestPage()
        page.attributes["key1"] = "value1"
        page.attributes["key2"] = 42

        assertEquals("value1", page.attributes["key1"])
        assertEquals(42, page.attributes["key2"])
    }

    @Test
    fun `test before middleware with KClass registration`() {
        class TestBeforeRelay : RelayBefore {
            override val priority = 5

            override fun processBefore(requestDTO: Result<RequestDTO>): ResponseDTO =
                buildResponse<String> {
                    status = 403
                    statusText = "Forbidden"
                    body = "blocked"
                }
        }

        val page = TestPage()
        page.before(TestBeforeRelay::class as KClass<RelayBefore>)
        val req = buildRequest { method = Method.GET }

        val resp = page.middlewareProcessBefore(req)
        assertNotNull(resp)
        assertEquals(403, resp?.status)
    }

    @Test
    fun `test after middleware with instance registration`() {
        var wasCalled = false
        val afterRelay = relayAfter { wasCalled = true }

        val page = TestPage()
        page.after(afterRelay)
        val req = buildRequest { method = Method.GET }

        val resp = page.content(req, emptyMap())
        page.middlewareProcessAfter(resp.toResult())

        assertTrue(wasCalled)
    }

    @Test
    fun `test multiple before middlewares execute in order`() {
        val page = TestPage()
        val callOrder = mutableListOf<Int>()

        page.before(
            relayBefore(priority = 10) {
                callOrder.add(1)
                null
            },
        )
        page.before(
            relayBefore(priority = 5) {
                callOrder.add(2)
                null
            },
        )
        page.before(
            relayBefore(priority = 15) {
                callOrder.add(3)
                null
            },
        )

        val req = buildRequest { method = Method.GET }
        page.middlewareProcessBefore(req)

        assertEquals(listOf(1, 2, 3), callOrder)
    }

    @Test
    fun `test middleware chain stops when one returns response`() {
        val page = TestPage()
        var secondCalled = false

        page.before(
            relayBefore(priority = 10) {
                ok("stopped")
            },
        )
        page.before(
            relayBefore(priority = 5) {
                secondCalled = true
                null
            },
        )

        val req = buildRequest { method = Method.GET }
        val resp = page.middlewareProcessBefore(req)

        assertNotNull(resp)
        assertEquals("stopped", resp.body.body as String)
        assertEquals(false, secondCalled)
    }

    @Test
    fun `test multiple after middlewares all execute`() {
        val page = TestPage()
        val callCount = mutableListOf<Int>()

        page.after(relayAfter { callCount.add(1) })
        page.after(relayAfter { callCount.add(2) })
        page.after(relayAfter { callCount.add(3) })

        val req = buildRequest { method = Method.GET }
        val resp = page.content(req, emptyMap())
        page.middlewareProcessAfter(resp.toResult())

        assertEquals(3, callCount.size)
    }

    @Test
    fun `test after middleware receives success result`() {
        val page = TestPage()
        var receivedStatus: Int? = null

        page.after(
            relayAfter {
                receivedStatus = it.getOrNull()?.status
            },
        )

        val req = buildRequest { method = Method.GET }
        val resp = page.content(req, emptyMap())
        page.middlewareProcessAfter(resp.toResult())

        assertEquals(200, receivedStatus)
    }

    @Test
    fun `test after middleware receives failure result`() {
        val page = TestPage()
        var receivedError: Throwable? = null

        page.after(
            relayAfter {
                receivedError = it.exceptionOrNull()
            },
        )

        val req = buildRequest { method = Method.GET }
        val error = RuntimeException("test error")
        page.middlewareProcessAfter(Result.failure(error))

        assertEquals(error, receivedError)
    }

    @Test
    fun `test middleware before sets request on returned response`() {
        val page = TestPage()
        page.before(relayBefore { ok("response") })
        val req =
            buildRequest {
                method = Method.GET
                target = "/test"
            }

        val resp = page.middlewareProcessBefore(req)
        assertNotNull(resp)
        assertEquals(req, resp?._request)
    }

    @Test
    fun `test exceptionPage factory creates proper page`() {
        val page =
            exceptionPage { exception ->
                buildResponse<String> {
                    status = 500
                    statusText = "Internal Server Error"
                    body = exception.message ?: "Unknown error"
                }
            }

        val resp = page.content(buildRequest {  }.apply {
            attributes["exception"] = RuntimeException("Test exception")
        }, emptyMap())

        assertEquals(500, resp.status)
        assertEquals("Test exception", resp.body.body as String)
    }

    @Test
    fun `test notFoundPage factory creates proper page`() {
        val page =
            notFoundPage { request, _ ->
                buildResponse<String> {
                    status = 404
                    statusText = "Not Found"
                    body = "Page not found: ${request.target}"
                }
            }

        val req =
            buildRequest {
                method = Method.GET
                target = "/missing"
            }
        val resp = page.content(req, emptyMap())

        assertEquals(404, resp.status)
        assertEquals("Page not found: /missing", resp.body.body as String)
    }

    @Test
    fun `test exceptionPage has empty target`() {
        val page =
            exceptionPage {
                ok("error")
            }
        assertEquals("", page.target)
    }

    @Test
    fun `test middleware priority affects execution`() {
        val page = TestPage()
        val order = mutableListOf<String>()

        // Lower priority should execute later, but relays are sorted by descending priority
        page.before(
            relayBefore(10) {
                order.add("high")
                null
            },
        )
        page.before(
            relayBefore(1) {
                order.add("low")
                null
            },
        )
        page.before(
            relayBefore(5) {
                order.add("med")
                null
            },
        )

        val req = buildRequest { method = Method.GET }
        page.middlewareProcessBefore(req)

        assertEquals(listOf("high", "low", "med"), order)
    }

    @Test
    fun `test before middleware returning null continues to content`() {
        val page = TestPage()
        page.before(relayBefore { null })
        page.before(relayBefore { null })

        val req = buildRequest { method = Method.GET }
        val middlewareResp = page.middlewareProcessBefore(req)
        assertNull(middlewareResp)

        // Should proceed to content
        val resp = page.content(req, emptyMap())
        assertEquals(200, resp.status)
    }

    @Test
    fun `test attributes isolated per page instance`() {
        val page1 = TestPage()
        val page2 = TestPage()

        page1.attributes["key"] = "value1"
        page2.attributes["key"] = "value2"

        assertEquals("value1", page1.attributes["key"])
        assertEquals("value2", page2.attributes["key"])
    }

    @Test
    fun `test relay priority default is zero`() {
        val relay = relayBefore { null }
        assertEquals(0, relay.priority)
    }

    @Test
    fun `test relay with explicit priority`() {
        val relay = relayBefore(priority = 100) { null }
        assertEquals(100, relay.priority)
    }
}
