package test

import io.voidx.dto.Cookie
import io.voidx.dto.ResponseBody
import io.voidx.dto.SameSite
import io.voidx.dto.cookie
import io.voidx.dto.ok
import io.voidx.dto.writeHTTP
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CookieAndResponseCookieTests {
    @Test
    fun cookie_dsl_builds_and_serializes_all_attributes() {
        val c =
            cookie {
                name = "sid"
                value = "abc123"
                path = "/"
                maxAge = 3600
                httpOnly = true
                secure = true
                sameSite = SameSite.STRICT
            }

        val s = c.toString()
        assertTrue(s.contains("sid=abc123"))
        assertTrue(s.contains("Path=/"))
        assertTrue(s.contains("Max-Age=3600"))
        assertTrue(s.contains("HttpOnly"))
        assertTrue(s.contains("Secure"))
        assertTrue(s.contains("SameSite=STRICT"))
    }

    @Test
    fun writeHTTP_emits_set_cookie_headers_for_response_cookies() {
        val ck = Cookie("user", "neo", "/", null, httpOnly = false, secure = false, sameSite = SameSite.LAX)
        val resp = ok("hi", mutableMapOf("Content-Type" to "text/plain"), cookies = listOf(ck))

        val out = ByteArrayOutputStream()
        out.writeHTTP(resp, 1.1)
        val raw = out.toString().replace("\r\n", "\n")

        assertTrue(raw.startsWith("HTTP/1.1 200 OK\n"))
        assertTrue(raw.lines().any { it.startsWith("Set-Cookie: user=neo;") })
        val body = raw.substringAfter("\n\n")
        assertEquals("hi", body)
    }
}
