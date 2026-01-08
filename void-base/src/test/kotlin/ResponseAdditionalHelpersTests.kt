package test

import io.voidx.dto.*
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResponseAdditionalHelpersTests {
    @Test
    fun status_shortcuts_set_expected_codes_and_texts_and_cookies() {
        val ck = Cookie("sid", "1", "/", null, false, false, SameSite.LAX)
        val cases =
            listOf(
                unauthorized("u", cookies = listOf(ck)) to (401 to "Unauthorized"),
                forbidden("f") to (403 to "Forbidden"),
                notFound("n") to (404 to "Not Found"),
                conflict("c") to (409 to "Conflict"),
                tooManyRequests("t") to (429 to "Too Many Requests"),
                internalServerError("e") to (500 to "Internal Server Error"),
                notImplemented("ni") to (501 to "Not Implemented"),
                badGateway("bg") to (502 to "Bad Gateway"),
                serviceUnavailable("su") to (503 to "Service Unavailable"),
                gatewayTimeout("gt") to (504 to "Gateway Timeout"),
                teapot() to (418 to "I'm a teapot"),
            )

        cases.forEach { (resp, expect) ->
            assertEquals(expect.first, resp.status)
            assertEquals(expect.second, resp.statusText)
        }

        // Also ensure Set-Cookie is emitted for at least one of them
        val out = ByteArrayOutputStream()
        out.writeHTTP(cases.first().first, 1.1)
        val raw = out.toString().replace("\r\n", "\n")
        assertTrue(raw.lines().any { it.startsWith("Set-Cookie: sid=1;") })
    }
}
