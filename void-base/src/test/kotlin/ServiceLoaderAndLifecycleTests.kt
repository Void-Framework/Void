package test

import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.handle
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class SockSL(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

// A test module discovered via ServiceLoader (see META-INF/services) will register the /sl route.
class ServiceLoaderAndLifecycleTests {
    @Test
    fun service_loader_module_registers_route() {
        val r = router { }
        val srv = Server(r, 1.1)
        val sock = SockSL("GET /sl HTTP/1.1\r\nHost: x\r\n\r\n")
        sock.handle(srv, r)
        val out = sock.text()
        assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
        assertTrue(out.endsWith("\n\nsl"), out)
    }

    @Test
    fun lifecycle_hooks_fire_to_registered_module() {
        data class Counts(var before: Int = 0, var after: Int = 0, var shutdown: Int = 0)
        val counts = Counts()
        val mod = object : Bootstrap.Module {
            override fun beforeServerStart(serverKind: Bootstrap.ServerKind, port: Int) {
                counts.before += 1
            }
            override fun afterServerStart(serverKind: Bootstrap.ServerKind, port: Int) {
                counts.after += 1
            }
            override fun onShutdown() {
                counts.shutdown += 1
            }
        }
        Bootstrap.register(mod)

        // Fire lifecycle events directly (unit-level verification)
        Bootstrap.fireBeforeServerStart(Bootstrap.ServerKind.HTTP, 8080)
        Bootstrap.fireAfterServerStart(Bootstrap.ServerKind.HTTP, 8080)
        Bootstrap.fireShutdown()

        assertEquals(1, counts.before)
        assertEquals(1, counts.after)
        assertEquals(1, counts.shutdown)
    }
}
