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

private class SockSCR(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class BootstrapServeClasspathResourcesTests {
    @Test
    fun serve_directory_with_various_content_types_and_nested_paths() {
        // Register a bootstrap module that exposes the test resources folder "public" under /s
        val module = object : Bootstrap.Module {
            override fun onRouterCreated(ctx: Bootstrap.Context) {
                ctx.serveClasspathResources("/s", "public")
            }
        }
        Bootstrap.register(module)

        val r = router { }
        val srv = Server(r, 1.1)

        fun fetch(path: String): String {
            val sock = SockSCR("GET $path HTTP/1.1\r\nHost: x\r\n\r\n")
            sock.handle(srv, r)
            return sock.text()
        }

        val cases = listOf(
            "/s/index.html" to "text/html",
            "/s/style.css" to "text/css",
            "/s/app.js" to "application/javascript",
            "/s/data.json" to "application/json",
            "/s/image.svg" to "image/svg+xml",
            "/s/readme.txt" to "text/plain",
            "/s/nested/deep.txt" to "text/plain",
        )

        for ((path, ct) in cases) {
            val out = fetch(path)
            assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
            assertTrue(out.contains("Content-Type: $ct\n"), out)
            // Validate we have a non-zero content-length rather than decoding body (binary files)
            val cl = Regex("Content-Length: (\\d+)")
                .find(out)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            assertTrue(cl > 0, "Content-Length should be > 0 for $path. Raw: $out")
        }

        // Binary types: assert headers only (test resources may contain stub bytes)
        val binaryCases = listOf(
            "/s/img.png" to "image/png",
            "/s/pic.jpg" to "image/jpeg",
            "/s/anim.gif" to "image/gif",
        )
        for ((path, ct) in binaryCases) {
            val out = fetch(path)
            assertTrue(out.startsWith("HTTP/1.1 200 OK\n"), out)
            assertTrue(out.contains("Content-Type: $ct\n"), out)
        }

        // A missing file under the prefix should yield 404 from router
        val miss = fetch("/s/nope.css")
        assertTrue(miss.startsWith("HTTP/1.1 404"), miss)
    }
}
