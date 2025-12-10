package test

import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.bootstrap.Event
import io.voidx.dto.ok
import io.voidx.handle
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

private class TestSocketEvents(
    private val incoming: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(incoming.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class BootstrapEventTests {
    @Test
    fun router_created_and_page_added_events_fire() {
        val events = mutableListOf<Event>()
        val listener: (Event) -> Unit = { events += it }
        Bootstrap.registerListener(listener)
        try {
            val r = router { }
            // Add a simple route
            r.addRoute(
                route("/ev1") {
                    GET { ok("x") }
                },
            )

            // Validate that RouterCreated and PageAdded have been observed in order
            // Allow other events possibly emitted elsewhere, but ensure these exist and order
            val createdIdx = events.indexOfFirst { it is Event.RouterCreated }
            val pageAddedIdx = events.indexOfFirst { it is Event.PageAdded }
            assertTrue(createdIdx >= 0, "RouterCreated not emitted: $events")
            assertTrue(pageAddedIdx >= 0, "PageAdded not emitted: $events")
            assertTrue(createdIdx <= pageAddedIdx, "RouterCreated should come before PageAdded: $events")
        } finally {
            Bootstrap.unregisterListener(listener)
        }
    }

    @Test
    fun request_lifecycle_events_in_order_for_normal_route() {
        val seen = mutableListOf<String>()
        val listener: (Event) -> Unit = {
            when (it) {
                is Event.RequestStart -> seen += "start"
                is Event.BeforeGlobalMiddleware -> seen += "before"
                is Event.AfterGlobalMiddleware -> seen += "after"
                is Event.RequestEnd -> seen += "end"
                else -> {}
            }
        }
        Bootstrap.registerListener(listener)
        try {
            val r = router { }
            r.addRoute(
                route("/hello") {
                    GET { ok("hi") }
                },
            )

            val sock =
                TestSocketEvents(
                    "GET /hello HTTP/1.1\r\n" +
                        "Host: example.com\r\n\r\n",
                )
            val srv = Server(r, 1.1)
            sock.handle(srv, r)

            // Order must be: start -> before -> after -> end
            // There should be exactly one of each
            assertEquals(listOf("start", "before", "after", "end"), seen)
        } finally {
            Bootstrap.unregisterListener(listener)
        }
    }

    @Test
    fun error_event_emitted_from_router_error() {
        val events = mutableListOf<Event>()
        val listener: (Event) -> Unit = { events += it }
        Bootstrap.registerListener(listener)
        try {
            val r = router { }
            // Prepare socket and server
            val sock = TestSocketEvents("")
            val srv = Server(r, 1.1)
            val ch = io.voidx.ClientHandler(sock, srv, r)
            // Trigger router error directly
            r.error(ch, RuntimeException("boom"))

            assertTrue(events.any { it is Event.Error }, "Error event not emitted: $events")
        } finally {
            Bootstrap.unregisterListener(listener)
        }
    }

    @Test
    fun page_before_after_events_for_static_route() {
        val events = mutableListOf<Event>()
        val listener: (Event) -> Unit = { events += it }
        Bootstrap.registerListener(listener)
        try {
            val r = router { }
            r.addRoute(
                route("/s") {
                    GET { ok("S") }
                },
            )

            val sock =
                TestSocketEvents(
                    "GET /s HTTP/1.1\r\n" +
                        "Host: example.com\r\n\r\n",
                )
            val srv = Server(r, 1.1)
            sock.handle(srv, r)

            val pageBefore = events.filterIsInstance<Event.PageBefore>()
            val pageAfter = events.filterIsInstance<Event.PageAfter>()
            // Exactly one before and one after for the static page
            assertEquals(1, pageBefore.size, "Expected one PageBefore, got ${'$'}pageBefore")
            assertEquals(1, pageAfter.size, "Expected one PageAfter, got ${'$'}pageAfter")
        } finally {
            Bootstrap.unregisterListener(listener)
        }
    }

    @Test
    fun page_before_after_events_for_dynamic_route() {
        val events = mutableListOf<Event>()
        val listener: (Event) -> Unit = { events += it }
        Bootstrap.registerListener(listener)
        try {
            val r = router { }
            r.addRoute(
                route("/u/{id}") {
                    GET { ok("D") }
                },
            )

            val sock =
                TestSocketEvents(
                    "GET /u/42 HTTP/1.1\r\n" +
                        "Host: example.com\r\n\r\n",
                )
            val srv = Server(r, 1.1)
            sock.handle(srv, r)

            val pageBefore = events.filterIsInstance<Event.PageBefore>()
            val pageAfter = events.filterIsInstance<Event.PageAfter>()
            // Exactly one before and one after for the dynamic page
            assertEquals(1, pageBefore.size, "Expected one PageBefore, got ${'$'}pageBefore")
            assertEquals(1, pageAfter.size, "Expected one PageAfter, got ${'$'}pageAfter")
        } finally {
            Bootstrap.unregisterListener(listener)
        }
    }

    @Test
    fun addListener_returns_closeable_to_unregister() {
        val events = mutableListOf<Event>()
        val handle = Bootstrap.addListener { events += it }
        try {
            // Creating a router emits RouterCreated
            val r1 = router { }
            // Unregister listener
            handle.close()
            // Creating another router should not add more events
            val r2 = router { }

            val createdCount = events.count { it is Event.RouterCreated }
            assertEquals(1, createdCount, "Listener should have been unregistered; events=${'$'}events")
        } finally {
            // In case close failed earlier, try to unregister by closing again
            try { handle.close() } catch (_: Throwable) {}
        }
    }
}
