package test

import io.voidx.dto.http.RequestDTO
import java.io.ByteArrayInputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeSocket(private val input: ByteArrayInputStream) : Socket() {
    override fun getInputStream() = input
}

class RequestParseTests {

    @Test
    fun parse_ignores_body_for_get_even_with_content_length() {
        val raw = (
            "GET /hello HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Content-Length: 5\r\n" +
                "\r\n" +
                "HELLO"
        )
        val input = ByteArrayInputStream(raw.toByteArray())
        val client = FakeSocket(input)
        // Use overload without ClientHandler to keep test isolated from server
        val req = RequestDTO.parse(input)
        assertEquals("/hello", req.target)
        // Body should be ignored for GET
        assertEquals("", req.body)
    }

    @Test
    fun parse_reads_body_for_post_with_content_length() {
        val raw = (
            "POST /submit HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Content-Length: 11\r\n" +
                "\r\n" +
                "hello world"
        )
        val input = ByteArrayInputStream(raw.toByteArray())
        val client = FakeSocket(input)
        val req = RequestDTO.parse(input)
        assertEquals("/submit", req.target)
        assertEquals("hello world", req.body)
    }

    @Test
    fun parse_with_invalid_request_line_returns_default_get() {
        val raw = (
            "BROKEN\r\n" +
                "Host: example.com\r\n" +
                "\r\n"
            )
        val input = ByteArrayInputStream(raw.toByteArray())
        val req = RequestDTO.parse(input)
        // Defaults applied by RequestDTO.parse overload without ClientHandler
        assertEquals("/", req.target)
        assertEquals("GET", req.method.name)
        assertEquals("", req.body)
    }
}
