package test

import io.voidx.dto.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResponseAndCookieTests {
    @Test
    fun string_response_builder_populates_fields_and_headers_and_cookies() {
        val resp =
            buildResponse<String> {
                status = 201
                statusText = "Created"
                headers["Content-Type"] = "text/plain"
                cookies.add(
                    Cookie(
                        name = "sid",
                        value = "abc",
                        path = "/",
                        maxAge = 60,
                        httpOnly = true,
                        secure = false,
                        sameSite = SameSite.LAX,
                    ),
                )
                body = "ok"
            }

        assertEquals(201, resp.status)
        assertEquals("Created", resp.statusText)
        assertEquals("text/plain", resp.headers["Content-Type"])
        assertIs<ResponseBody.StringBody>(resp.body)
        assertEquals("ok", (resp.body as ResponseBody.StringBody).body)
        assertEquals(1, resp.cookies.size)
        assertTrue(resp.cookies[0].toString().startsWith("sid=abc"))
    }

    @Test
    fun byte_array_response_builder_path_is_used() {
        val bytes = "hello".toByteArray()
        val resp =
            buildResponse<ByteArray> {
                status = 200
                statusText = "OK"
                headers["Content-Type"] = "application/octet-stream"
                body = bytes
            }
        assertIs<ResponseBody.ByteArrayBody>(resp.body)
        assertEquals("hello", String((resp.body as ResponseBody.ByteArrayBody).body))
        assertEquals("application/octet-stream", resp.headers["Content-Type"])
    }

    @Test
    fun cookie_dsl_builds_and_serializes_expected_attributes() {
        val c =
            cookie {
                name = "token"
                value = "xyz"
                path = "/"
                maxAge = 300
                httpOnly = true
                secure = true
                sameSite = SameSite.STRICT
            }
        val s = c.toString()
        assertTrue(s.contains("token=xyz"))
        assertTrue(s.contains("Path=/"))
        assertTrue(s.contains("Max-Age=300"))
        assertTrue(s.contains("HttpOnly"))
        assertTrue(s.contains("Secure"))
        assertTrue(s.contains("SameSite=STRICT"))
    }
}
