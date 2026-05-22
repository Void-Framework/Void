package test

import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.ok
import io.voidx.handle
import io.voidx.middleware.relayAfter
import io.voidx.page.Page
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

private class TestSocketHooks(
    private val incoming: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(incoming.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class BootstrapHookTests {
    @Test
    fun page_decorator_runs_for_added_pages() {
        val targets = mutableListOf<String>()
        val handle = Bootstrap.addPageDecorator { page: Page, _ -> targets += page.target }
        try {
            val r = router { }
            r.addRoute(route("/a") { GET { _, _ -> ok("A") } })
            r.addRoute(route("/b/{id}") { GET { _, _ -> ok("B") } })

            assertTrue("/a" in targets, "Decorator not called for static page")
            assertTrue("/b/{id}" in targets, "Decorator not called for dynamic page")
        } finally {
            handle.close()
        }
    }

    @Test
    fun error_handler_invoked_from_router_error() {
        var called = 0
        val handle = Bootstrap.addErrorHandler { _, _ -> called += 1 }
        try {
            val r = router { }
            val sock = TestSocketHooks("")
            val srv = Server(r, 1.1)
            val ch = io.voidx.ClientHandler(sock, srv, r)
            r.error(ch, RuntimeException("boom"))

            assertEquals(1, called, "Error handler should be invoked once")
        } finally {
            handle.close()
        }
    }

    @Test
    fun addPageDecorator_returns_closeable_to_unregister() {
        var count = 0
        val handle = Bootstrap.addPageDecorator { _, _ -> count += 1 }
        try {
            val r = router { }
            r.addRoute(route("/x") { GET { _, _ -> ok("x") } })
            handle.close()
            r.addRoute(route("/y") { GET { _, _ -> ok("y") } })
            assertEquals(2, count, "Count should've increased even after closing")
        } finally {
            try {
                handle.close()
            } catch (_: Throwable) {
            }
        }
    }

    @Test
    fun addErrorHandler_returns_closeable_to_unregister() {
        var count = 0
        val handle = Bootstrap.addErrorHandler { _, _ -> count += 1 }
        try {
            val r = router { }
            val sock = TestSocketHooks("")
            val srv = Server(r, 1.1)
            val ch = io.voidx.ClientHandler(sock, srv, r)
            r.error(ch, RuntimeException("boom"))
            handle.close()
            r.error(ch, RuntimeException("boom2"))
            assertEquals(1, count, "Error handler should have been unregistered by handle")
        } finally {
            try {
                handle.close()
            } catch (_: Throwable) {
            }
        }
    }

    @Test
    fun global_after_runs_in_short_circuit_and_decorators_are_unrelated() {
        val r = router { }
        val calls = mutableListOf<String>()
        with(r) {
            +relayAfter(priority = 1) { resp -> if (resp.isSuccess) calls += "A1" }
            +relayAfter(priority = 10) { resp -> if (resp.isSuccess) calls += "A10" }
        }

        val sr = Bootstrap.addSpecialRoute(priority = 100) { _, _, _ -> ok("s") }
        try {
            val sock = TestSocketHooks("GET /p HTTP/1.1\r\nHost: x\r\n\r\n")
            val srv = Server(r, 1.1)
            sock.handle(srv, r)
            assertEquals(listOf("A10", "A1"), calls)
        } finally {
            sr.close()
        }
    }
}
