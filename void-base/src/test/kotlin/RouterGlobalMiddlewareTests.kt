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
        sock.handle(1.1, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 401 Unauthorized\n"), raw)

        // BEFORE: only B10 should run (and short-circuit), B1 should not run
        // AFTER: runs in priority order (descending): A10 then A1
        assertEquals(listOf("B10", "A10", "A1"), calls)
    }

    @Test
    fun relay_before_receives_normalized_target_preventing_auth_bypass() {
        val r = router { }

        with(r) {
            +relayBefore { result ->
                val req = result.getOrNull() ?: return@relayBefore null
                if (req.target == "/admin" || req.target.startsWith("/admin?") || req.target.startsWith("/admin/")) {
                    buildResponse<String> {
                        status = 403
                        statusText = "Forbidden"
                        headers["Content-Type"] = "text/plain"
                        body = "access-denied"
                    }
                } else {
                    null
                }
            }
        }

        r.addRoute(
            route("/admin") {
                GET { _, _ -> ok("admin-secret", mutableMapOf("Content-Type" to "text/plain")) }
            },
        )

        val pathsToTest = listOf("//admin", "///admin", "/admin//", "//admin?query=1")
        for (path in pathsToTest) {
            val sock =
                MemSocket(
                    "GET $path HTTP/1.1\r\n" +
                        "Host: example.com\r\n\r\n",
                )
            sock.handle(1.1, r)
            val raw = sock.text()
            assertTrue(raw.startsWith("HTTP/1.1 403 Forbidden\n"), "Path $path should have been blocked by auth: $raw")
            assertTrue(raw.contains("access-denied"), "Response for $path should contain access-denied body: $raw")
        }
    }

    @Test
    fun relay_before_and_page_handler_receive_consistent_normalized_target() {
        val r = router { }
        var middlewareTarget: String? = null
        var pageTarget: String? = null

        with(r) {
            +relayBefore { result ->
                middlewareTarget = result.getOrNull()?.target
                null
            }
        }

        r.addRoute(
            route("/users/profile") {
                GET { req, _ ->
                    pageTarget = req.target
                    ok("profile")
                }
            },
        )

        val sock =
            MemSocket(
                "GET //users//profile?id=42 HTTP/1.1\r\n" +
                    "Host: example.com\r\n\r\n",
            )
        sock.handle(1.1, r)

        assertEquals("/users/profile?id=42", middlewareTarget)
        assertEquals("/users/profile?id=42", pageTarget)
    }
}
