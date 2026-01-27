package test

import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.handle
import io.voidx.page.path
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

private class DynSock(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class RouterDynamicAndMethodDispatchTests {
    @Test
    fun dynamic_route_optional_segment_missing_matches_and_binds_only_required() {
        val r = router { }
        r.addRoute(
            route("/u/{id}/{name?}") {
                GET {
                    // Access dynamic path variables via helper (implicit DynamicPage receiver)
                    val id: String? = path<String>("id")
                    val name: String? = path<String>("name?")
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "$id:${name ?: "-"}"
                    }
                }
            },
        )

        val sock =
            DynSock(
                "GET /u/42 HTTP/1.1\r\n" +
                    "Host: example.com\r\n\r\n",
            )
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 200 OK\n"), raw)
        assertTrue(raw.contains("Content-Type: text/plain\n"), raw)
        assertEquals("42:-", raw.substringAfter("\n\n"))
    }

    @Test
    fun page_handler_dispatches_by_method_get_vs_post() {
        val r = router { }
        r.addRoute(
            route("/m") {
                GET {
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "g"
                    }
                }
                POST {
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "p"
                    }
                }
            },
        )

        val srv = Server(r, 1.1)

        val getSock =
            DynSock(
                "GET /m HTTP/1.1\r\nHost: x\r\n\r\n",
            )
        getSock.handle(srv, r)
        val getResp = getSock.text()
        assertTrue(getResp.startsWith("HTTP/1.1 200 OK\n"), getResp)
        assertEquals("g", getResp.substringAfter("\n\n"))

        val postSock =
            DynSock(
                "POST /m HTTP/1.1\r\nHost: x\r\nContent-Length: 0\r\n\r\n",
            )
        postSock.handle(srv, r)
        val postResp = postSock.text()
        assertTrue(postResp.startsWith("HTTP/1.1 200 OK\n"), postResp)
        assertEquals("p", postResp.substringAfter("\n\n"))
    }

    @Test
    fun dynamic_route_matches_with_trailing_slash() {
        val r = router { }
        r.addRoute(
            route("/u/{id}") {
                GET {
                    val id: String? = path<String>("id")
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "id=$id"
                    }
                }
            },
        )

        val sock =
            DynSock(
                "GET /u/42/ HTTP/1.1\r\nHost: x\r\n\r\n",
            )
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 200 OK\n"), raw)
        assertEquals("id=42", raw.substringAfter("\n\n"))
    }

    @Test
    fun dynamic_routes_dont_ignore_favicon() {
        val r = router { }
        // This would normally match any single segment, including "favicon.ico" without the special-case bypass
        r.addRoute(
            route("/{name}") {
                GET {
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "text/plain"
                        body = "matched"
                    }
                }
            },
        )

        val sock =
            DynSock(
                "GET /favicon.ico HTTP/1.1\r\nHost: x\r\n\r\n",
            )
        val srv = Server(r, 1.1)
        sock.handle(srv, r)

        val raw = sock.text()
        assertTrue(raw.startsWith("HTTP/1.1 200 OK\n"), raw)
        assertEquals("matched", raw.substringAfter("\n\n"))
    }
}
