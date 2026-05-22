package test

import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.dto.ok
import io.voidx.handle
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
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

private class MemSocket(
    private val incoming: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(incoming.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class RouterGlobalMiddlewareTests {
    @Test
    fun relay_before_ordering_and_short_circuit_and_after_ordering() {
        val r = router { }
        val calls = mutableListOf<String>()

        // AFTER relays should still run even if BEFORE short-circuits
        with(r) {
            +relayAfter(priority = 1) { resp ->
                if (resp.isSuccess) calls += "A1"
            }
            +relayAfter(priority = 10) { resp ->
                if (resp.isSuccess) calls += "A10"
            }
        }

        // BEFORE relays: higher priority first; the highest will short-circuit
        with(r) {
            +relayBefore(priority = 10) { _ ->
                calls += "B10"
                buildResponse<String> {
                    status = 401
                    statusText = "Unauthorized"
                    headers["Content-Type"] = "text/plain"
                    body = "nope"
                }
            }
            +relayBefore(priority = 1) { _ ->
                calls += "B1"
                null
            }
        }

        // Register a page which should NOT be reached due to short-circuit
        r.addRoute(
            route("/x") {
                GET { _, _ -> ok("ok", mutableMapOf("Content-Type" to "text/plain")) }
            },
        )

        val sock =
            MemSocket(
                "GET /x HTTP/1.1\r\n" +
                    "Host: example.com\r\n\r\n",
            )
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 401 Unauthorized\n"), raw)

        // BEFORE: only B10 should run (and short-circuit), B1 should not run
        // AFTER: runs in priority order (descending): A10 then A1
        assertEquals(listOf("B10", "A10", "A1"), calls)
    }
}
