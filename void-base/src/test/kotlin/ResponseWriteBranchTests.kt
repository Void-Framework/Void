package test

import io.voidx.dto.SameSite
import io.voidx.dto.buildResponse
import io.voidx.dto.writeHTTP
import io.voidx.dto.cookie
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResponseWriteBranchTests {
    @Test
    fun preset_content_length_is_preserved_for_string_body() {
        val resp = buildResponse<String> {
            status = 200
            statusText = "OK"
            headers["Content-Type"] = "text/plain"
            headers["Content-Length"] = "5"
            body = "hello"
        }
        val out = ByteArrayOutputStream()
        out.writeHTTP(resp, 1.1)
        val raw = out.toString().replace("\r\n", "\n")
        // Our preset header should remain 5
        assertTrue(raw.contains("Content-Length: 5\n"), raw)
        assertTrue(raw.endsWith("\n\nhello"), raw)
    }

    @Test
    fun byte_array_body_sets_length_and_writes_cookies() {
        val payload = byteArrayOf(1, 2, 3, 4, 5)
        val resp = buildResponse<ByteArray> {
            status = 200
            statusText = "OK"
            headers["Content-Type"] = "application/octet-stream"
            body = payload
            cookies.add(
                cookie {
                    name = "a"; value = "1"; path = "/"; sameSite = SameSite.LAX
                },
            )
            cookies.add(
                cookie {
                    name = "b"; value = "2"; path = "/"; sameSite = SameSite.LAX
                },
            )
        }
        val out = ByteArrayOutputStream()
        out.writeHTTP(resp, 1.1)
        val rawHeaders = out.toString().substringBefore("\r\n\r\n").replace("\r\n", "\n")
        val cl = Regex("Content-Length: (\\d+)").find(rawHeaders)?.groupValues?.get(1)?.toInt() ?: 0
        assertEquals(payload.size, cl)
        assertTrue(rawHeaders.contains("Set-Cookie: a=1; Path=/"), rawHeaders)
        assertTrue(rawHeaders.contains("Set-Cookie: b=2; Path=/"), rawHeaders)
    }
}
