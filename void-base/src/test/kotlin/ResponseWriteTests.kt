package test

import io.voidx.dto.http.buildResponse
import io.voidx.dto.http.writeHTTP
import java.io.ByteArrayOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResponseWriteTests {
    @Test
    fun writeHTTP_adds_content_length_and_formats_status_line_for_string_body() {
        val resp =
            buildResponse<String> {
                status = 200
                statusText = "OK"
                // Intentionally omit Content-Length to verify auto-add
                headers["Content-Type"] = "text/plain"
                body = "hello"
            }

        val out = ByteArrayOutputStream()
        out.writeHTTP(resp, 1.1)
        val raw = out.toString()

        // Allow either CRLF or LF depending on platform writer implementation
        val normalized = raw.replace("\r\n", "\n")
        assertTrue(normalized.startsWith("HTTP/1.1 200 OK\n"), "Status line must start with HTTP/1.1 status line")
        assertTrue(normalized.contains("Content-Type: text/plain\n"))
        assertTrue(
            normalized.contains("Content-Length: 5\n"),
            "Content-Length should be auto-populated for string body",
        )

        // Headers/body separator and body value
        assertTrue(normalized.contains("\n\n"))
        assertTrue(normalized.endsWith("hello"))
    }

    @Test
    fun writeHTTP_adds_content_length_for_byte_array_body() {
        val bytes = "abcdef".toByteArray()
        val resp =
            buildResponse<ByteArray> {
                status = 201
                statusText = "Created"
                headers["Content-Type"] = "application/octet-stream"
                body = bytes
            }

        val out = ByteArrayOutputStream()
        out.writeHTTP(resp, 1.1)
        val raw = out.toString()

        val normalized = raw.replace("\r\n", "\n")
        assertTrue(normalized.startsWith("HTTP/1.1 201 Created\n"))
        assertTrue(normalized.contains("Content-Length: ${bytes.size}\n"))

        // Extract body and ensure it matches exactly
        val bodyPart = normalized.substringAfter("\n\n")
        assertEquals("abcdef", bodyPart)
    }
}
