package test

import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.handle
import io.voidx.page.exceptionPage
import io.voidx.page.notFoundPage
import io.voidx.page.route
import io.voidx.router.router
import io.voidx.router.util.RouteCheck
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertTrue

private class InlineSocket(
    private val incoming: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(incoming.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun output(): String = outBytes.toString().replace("\r\n", "\n")
}

class RouterNotFoundAndExceptionFlowTests {
    private var prevExceptionPage = RouteCheck.exceptionPage
    private var prevNullPage = RouteCheck.nullPage

    @BeforeEach
    fun setUp() {
        // Capture current globals to restore later for test isolation
        prevExceptionPage = RouteCheck.exceptionPage
        prevNullPage = RouteCheck.nullPage
    }

    @AfterEach
    fun tearDown() {
        // Restore global pages so tests don't leak state
        RouteCheck.exceptionPage = prevExceptionPage
        RouteCheck.nullPage = prevNullPage
    }

    @Test
    fun custom_not_found_page_overrides_default() {
        val r = router { }
        RouteCheck.nullPage =
            notFoundPage {
                buildResponse<String> {
                    status = 404
                    statusText = "Not Found"
                    headers["Content-Type"] = "text/plain"
                    body = "custom-404"
                }
            }

        val rawRequest = (
            "GET /missing HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "\r\n"
        )
        val sock = InlineSocket(rawRequest)
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.output()
        assertTrue(raw.startsWith("HTTP/1.1 404 Not Found\n"), raw)
        assertTrue(raw.endsWith("custom-404"), raw)
    }

    @Test
    fun exception_in_handler_triggers_exception_page_via_client_handler_flow() {
        val r = router { }
        // Ensure a known exception page with a recognizable marker is installed
        RouteCheck.exceptionPage =
            exceptionPage {
                buildResponse<String> {
                    status = 500
                    statusText = "Server Error"
                    headers["Content-Type"] = "text/html"
                    body = "<div id=\"__next-dev-overlay\"></div>"
                }
            }
        r.addRoute(
            route("/boom") {
                GET {
                    throw IllegalStateException("explode")
                }
            },
        )

        val rawRequest = (
            "GET /boom HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "\r\n"
        )
        val sock = InlineSocket(rawRequest)
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.output()
        assertTrue(raw.startsWith("HTTP/1.1 500 Server Error\n"), raw)
        // Our installed exception page includes overlay container id
        assertTrue(raw.contains("__next-dev-overlay"), raw)
    }

    @Test
    fun custom_exception_page_overrides_default_during_flow() {
        val r = router { }
        RouteCheck.exceptionPage =
            exceptionPage {
                buildResponse<String> {
                    status = 500
                    statusText = "Server Error"
                    headers["Content-Type"] = "text/plain"
                    body = "custom-500:${exception::class.simpleName}"
                }
            }

        r.addRoute(
            route("/oops") {
                GET {
                    error("Bang")
                }
            },
        )

        val rawRequest = (
            "GET /oops HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Connection: close\r\n" +
                "\r\n"
        )
        val sock = InlineSocket(rawRequest)
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.output()
        assertTrue(raw.startsWith("HTTP/1.1 500 Server Error\n"), raw)
        assertTrue(raw.contains("custom-500:IllegalStateException"), raw)
    }
}
