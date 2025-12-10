package test

import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.bootstrap.Event
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

        val hLow: (RequestDTO, Map<String, String>, io.voidx.ClientHandler) -> ResponseDTO? = { _, _, _ ->
            calls += "low"
            null
        }
        val hHigh: (RequestDTO, Map<String, String>, io.voidx.ClientHandler) -> ResponseDTO? = { _, _, _ ->
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
        r.addRoute(route("/p") { GET { ok("page") } })

        val sock = TestSocketSR("GET /p HTTP/1.1\r\nHost: x\r\n\r\n")
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

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

        val handle = Bootstrap.addSpecialRoute(priority = 100) { _, _, _ -> ok("x") }

        val sock = TestSocketSR("GET /whatever HTTP/1.1\r\nHost: x\r\n\r\n")
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        assertEquals(listOf("A10", "A5"), afterCalls)

        handle.close()
    }

    @Test
    fun no_page_events_for_special_routes() {
        val r = router { }
        val seen = mutableListOf<String>()
        val listener: (Event) -> Unit = {
            when (it) {
                is Event.PageBefore -> seen += "before"
                is Event.PageAfter -> seen += "after"
                else -> {}
            }
        }
        Bootstrap.registerListener(listener)
        val handle = Bootstrap.addSpecialRoute(priority = 42) { _, _, _ -> ok("z") }

        try {
            val sock = TestSocketSR("GET /anything HTTP/1.1\r\nHost: x\r\n\r\n")
            val srv = Server(r, 1.1)
            sock.handle(srv, r)

            // No per-page events expected since special route bypasses page system
            assertTrue(seen.isEmpty(), "Per-page events should not fire for special routes: $seen")
        } finally {
            handle.close()
            Bootstrap.unregisterListener(listener)
        }
    }

    @Test
    fun unregistration_via_handle() {
        val r = router { }
        var count = 0
        val handle = Bootstrap.addSpecialRoute(priority = 1) { _, _, _ ->
            count += 1
            ok("once")
        }

        val srv = Server(r, 1.1)
        val sock1 = TestSocketSR("GET /a HTTP/1.1\r\nHost: x\r\n\r\n")
        sock1.handle(srv, r)
        handle.close()
        val sock2 = TestSocketSR("GET /b HTTP/1.1\r\nHost: x\r\n\r\n")
        sock2.handle(srv, r)

        assertEquals(1, count, "Handler should be unregistered by handle.close()")
    }
}
