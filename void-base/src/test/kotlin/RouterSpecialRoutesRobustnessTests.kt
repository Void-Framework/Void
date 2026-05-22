package test

import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
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

private class SockSRR(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class RouterSpecialRoutesRobustnessTests {
    @Test
    fun throwing_high_priority_allows_lower_priority_to_handle() {
        val r = router { }
        // a normal page, should be bypassed by special route
        r.addRoute(route("/p") { GET { _, _ -> ok("page") } })

        // high priority throws
        val high: (RequestDTO, Map<String, String>) -> ResponseDTO? = { _, _ ->
            throw RuntimeException("boom")
        }
        // lower priority handles
        val low: (RequestDTO, Map<String, String>) -> ResponseDTO? = { _, _ -> ok("low") }

        Bootstrap.registerSpecialRoute(priority = 10, handler = high)
        Bootstrap.registerSpecialRoute(priority = 1, handler = low)
        try {
            val sock = SockSRR("GET /p HTTP/1.1\r\nHost: x\r\n\r\n")
            sock.handle(1.1, r)
            val out = sock.text()
            assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
            assertTrue(out.endsWith("\n\nlow"), out)
        } finally {
            Bootstrap.unregisterSpecialRoute(high)
            Bootstrap.unregisterSpecialRoute(low)
        }
    }

    @Test
    fun unregister_during_dispatch_is_safe_and_effective_next_request() {
        val r = router { }
        var count = 0
        lateinit var self: (RequestDTO, Map<String, String>) -> ResponseDTO?
        self = { _, _ ->
            count += 1
            // Unregister itself; current snapshot iteration should continue safely
            Bootstrap.unregisterSpecialRoute(self)
            null
        }
        val other = { _: RequestDTO, _: Map<String, String> -> ok("ok") }
        val h1 = Bootstrap.addSpecialRoute(priority = 5, handler = self)
        val h2 = Bootstrap.addSpecialRoute(priority = 1, handler = other)
        try {
            val sock1 = SockSRR("GET /a HTTP/1.1\r\nHost: x\r\n\r\n")
            sock1.handle(1.1, r)
            assertEquals(1, count, "self should run once during first dispatch")

            val sock2 = SockSRR("GET /b HTTP/1.1\r\nHost: x\r\n\r\n")
            sock2.handle(1.1, r)
            assertEquals(1, count, "self should not be invoked again after unregister")
        } finally {
            h1.close()
            h2.close()
        }
    }
}
