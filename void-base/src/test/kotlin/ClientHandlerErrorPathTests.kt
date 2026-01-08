package test

import io.voidx.ClientHandler
import io.voidx.Server
import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.buildResponse
import io.voidx.middleware.relayBefore
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class SockCHE(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class ClientHandlerErrorPathTests {
    @Test
    fun error_path_respects_global_before_short_circuit_and_skips_router_error() {
        val r = router { }

        // BEFORE middleware that short-circuits when error() is called
        with(r) {
            +relayBefore { err ->
                // When invoked from ClientHandler.error, the Result holds an Exception
                if (err.isFailure) {
                    buildResponse<String> {
                        status = 503
                        statusText = "Service Unavailable"
                        headers["Content-Type"] = "text/plain"
                        body = "short"
                    }
                } else {
                    null
                }
            }
        }

        // Track if Router.error path is taken via Bootstrap error handler
        var routerErrorCalled = false
        val h = Bootstrap.addErrorHandler { _, _ -> routerErrorCalled = true }
        try {
            val srv = Server(r, 1.1)
            val sock = SockCHE("GET /anything HTTP/1.1\r\nHost: x\r\n\r\n")
            // Simulate a request that triggers error handling
            val ch = ClientHandler(sock, srv, r)
            ch.error(RuntimeException("boom"))

            val out = sock.text()
            assertTrue(out.startsWith("HTTP/1.1 503 Service Unavailable\n"), out)
            assertTrue(out.endsWith("\n\nshort"), out)
            // Since BEFORE short-circuited, Router.error should not be invoked
            assertFalse(routerErrorCalled, "Router.error should be skipped when BEFORE short-circuits in error()")
        } finally {
            h.close()
        }
    }
}
