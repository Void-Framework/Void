package test

import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.dto.ok
import io.voidx.handle
import io.voidx.middleware.relayAfter
import io.voidx.page.route
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class TestSocketSR(
    private val incoming: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(incoming.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class RouterSpecialRoutesTests {
    @Test
    fun special_route_short_circuits_and_orders() {
        val r = router { }
        val calls = mutableListOf<String>()

        val hLow: (RequestDTO, Map<String, String>) -> ResponseDTO? = { _, _ ->
            calls += "low"
            null
        }
        val hHigh: (RequestDTO, Map<String, String>) -> ResponseDTO? = { _, _ ->
            calls += "high"
            buildResponse<String> {
                status = 200
                statusText = "OK"
                headers["Content-Type"] = "text/plain"
                body = "from-high"
            }
        }

        // Register in reverse order to ensure priority sorting is effective
        Bootstrap.registerSpecialRoute(priority = 1, handler = hLow)
        Bootstrap.registerSpecialRoute(priority = 10, handler = hHigh)

        // A page that should not be reached if special route short-circuits
        r.addRoute(route("/p") { GET { _, _ -> ok("page") } })

        val sock = TestSocketSR("GET /p HTTP/1.1\r\nHost: x\r\n\r\n")
        sock.handle(1.1, r)

        val out = sock.text()
        assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
        assertTrue(out.contains("from-high"), out)
        // Only the high priority should have produced, low should not run after short-circuit
        assertEquals(listOf("high"), calls)

        // cleanup
        Bootstrap.unregisterSpecialRoute(hLow)
        Bootstrap.unregisterSpecialRoute(hHigh)
    }

    @Test
    fun after_middleware_runs_even_on_special_short_circuit() {
        val r = router { }
        val afterCalls = mutableListOf<String>()
        with(r) {
            +relayAfter(priority = 5) { resp -> if (resp.isSuccess) afterCalls += "A5" }
            +relayAfter(priority = 10) { resp -> if (resp.isSuccess) afterCalls += "A10" }
        }

        val handle = Bootstrap.addSpecialRoute(priority = 100) { _, _ -> ok("x") }

        val sock = TestSocketSR("GET /whatever HTTP/1.1\r\nHost: x\r\n\r\n")
        sock.handle(1.1, r)

        assertEquals(listOf("A10", "A5"), afterCalls)

        handle.close()
    }

    @Test
    fun special_routes_bypass_page_before_after_middleware() {
        val r = router { }
        val pageCalls = mutableListOf<String>()
        // Register a page with per-page before/after middleware to observe calls
        val p =
            route("/anything") {
                GET { _, _ -> ok("P") }
            }.apply {
                before(
                    io.voidx.middleware.relayBefore { _ ->
                        pageCalls += "before"
                        null
                    },
                )
                after(io.voidx.middleware.relayAfter { resp -> if (resp.isSuccess) pageCalls += "after" })
            }
        r.addRoute(p)
        val handle = Bootstrap.addSpecialRoute(priority = 42) { _, _ -> ok("z") }

        try {
            val sock = TestSocketSR("GET /anything HTTP/1.1\r\nHost: x\r\n\r\n")
            sock.handle(1.1, r)

            // Per-page middleware should not run because special route short-circuited
            assertTrue(pageCalls.isEmpty(), "Per-page middleware should not run for special routes: $pageCalls")
        } finally {
            handle.close()
        }
    }

    @Test
    fun unregistration_via_handle() {
        val r = router { }
        var count = 0
        val handle =
            Bootstrap.addSpecialRoute(priority = 1) { _, _ ->
                count += 1
                ok("once")
            }

        val sock1 = TestSocketSR("GET /a HTTP/1.1\r\nHost: x\r\n\r\n")
        sock1.handle(1.1, r)
        handle.close()
        val sock2 = TestSocketSR("GET /b HTTP/1.1\r\nHost: x\r\n\r\n")
        sock2.handle(1.1, r)

        assertEquals(1, count, "Handler should be unregistered by handle.close()")
    }
}
