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

private class SockSCF(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class BootstrapServeClasspathFileTests {
    @Test
    fun serve_single_classpath_file_and_missing_file() {
        // Register a bootstrap module that exposes a classpath file and a missing one
        val module =
            object : Bootstrap.Module {
                override fun onRouterCreated(ctx: Bootstrap.Context) {
                    ctx.serveClasspathFile("/static/elements.json", "elements.json")
                    ctx.serveClasspathFile("/static/missing.js", "nope.js")
                }
            }
        Bootstrap.register(module)

        val r = router { }
        val srv = Server(r, 1.1)

        // Existing resource
        val okSock = SockSCF("GET /static/elements.json HTTP/1.1\r\nHost: x\r\n\r\n")
        okSock.handle(srv, r)
        val okOut = okSock.text()
        assertTrue(okOut.startsWith("HTTP/1.1 200 OK\n"), okOut)
        assertTrue(okOut.contains("Content-Type: application/json\n"), okOut)
        val okBody = okOut.substringAfter("\n\n")
        assertTrue(okBody.trim().startsWith("{"), "Body should be JSON: ${okBody.take(30)}")

        // Missing resource should 404
        val missSock = SockSCF("GET /static/missing.js HTTP/1.1\r\nHost: x\r\n\r\n")
        missSock.handle(srv, r)
        val missOut = missSock.text()
        assertTrue(missOut.startsWith("HTTP/1.1 404 Not Found\n"), missOut)
        assertTrue(missOut.contains("Content-Type: text/plain\n"), missOut)
        val missBody = missOut.substringAfter("\n\n").trim()
        assertEquals("Not Found", missBody)
    }
}
