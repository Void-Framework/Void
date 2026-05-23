package test

import io.voidx.Method
import io.voidx.dto.RequestDTO
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for the changed RequestDTO.parse() behavior introduced in this PR:
 * - When the input stream yields null on readLine (empty stream), returns a minimal GET "/"
 *   request and sets attributes["Malformed"] = true.
 * - When the request line has fewer than two tokens, returns a minimal request WITHOUT
 *   setting Malformed (different code path).
 * - For well-formed requests, Malformed attribute is not set.
 */
class RequestParseMalformedTests {

    @Test
    fun empty_stream_sets_malformed_attribute_true() {
        val input = ByteArrayInputStream(ByteArray(0))
        val result = RequestDTO.parse(input)
        val malformed = result.attributes["Malformed"] as? Boolean

        assertEquals(
            true,
            malformed,
            "Expected Malformed=true for empty stream, but was: ${result.attributes["Malformed"]}",
        )
        assertEquals(Method.GET, result.method)
        assertEquals("/", result.target)
        assertEquals("", result.body)
    }

    @Test
    fun empty_stream_produces_fallback_get_request() {
        val input = ByteArrayInputStream(ByteArray(0))
        val result = RequestDTO.parse(input)

        assertEquals(Method.GET, result.method)
        assertEquals("/", result.target)
        assertEquals("", result.body)
    }

    @Test
    fun valid_get_request_does_not_set_malformed_attribute() {
        val raw = "GET /hello HTTP/1.1\r\nHost: localhost\r\n\r\n"
        val input = ByteArrayInputStream(raw.toByteArray())
        val result = RequestDTO.parse(input)

        assertFalse(
            (result.attributes["Malformed"] as? Boolean) ?: false,
            "Malformed should not be set for a well-formed request",
        )
        assertEquals(Method.GET, result.method)
        assertEquals("/hello", result.target)
    }

    @Test
    fun valid_post_request_does_not_set_malformed_attribute() {
        val raw = "POST /submit HTTP/1.1\r\nHost: localhost\r\nContent-Length: 4\r\n\r\nbody"
        val input = ByteArrayInputStream(raw.toByteArray())
        val result = RequestDTO.parse(input)

        assertFalse(
            (result.attributes["Malformed"] as? Boolean) ?: false,
            "Malformed should not be set for a valid POST request",
        )
        assertEquals(Method.POST, result.method)
        assertEquals("/submit", result.target)
        assertEquals("body", result.body)
    }

    @Test
    fun invalid_method_returns_fallback_without_malformed_attribute() {
        // A request line with unknown method - code path catches the IllegalArgumentException
        // but does NOT set Malformed attribute in this branch
        val raw = "BADMETHOD /path HTTP/1.1\r\nHost: localhost\r\n\r\n"
        val input = ByteArrayInputStream(raw.toByteArray())
        val result = RequestDTO.parse(input)

        // Falls back to GET "/" but Malformed is not set in this code path
        assertEquals(Method.GET, result.method)
        assertEquals("/", result.target)
    }

    @Test
    fun request_with_headers_parses_correctly() {
        val raw =
            "GET /api/items HTTP/1.1\r\n" +
                "Host: example.com\r\n" +
                "Authorization: Bearer token123\r\n" +
                "Accept: application/json\r\n" +
                "\r\n"
        val input = ByteArrayInputStream(raw.toByteArray())
        val result = RequestDTO.parse(input)

        assertFalse(result.attributes["Malformed"] == true)
        assertEquals(Method.GET, result.method)
        assertEquals("/api/items", result.target)
        assertEquals("example.com", result.headers["Host"])
        assertEquals("Bearer token123", result.headers["Authorization"])
        assertEquals("application/json", result.headers["Accept"])
    }

    @Test
    fun request_with_query_string_preserves_full_target() {
        val raw = "GET /search?q=kotlin&page=2 HTTP/1.1\r\nHost: localhost\r\n\r\n"
        val input = ByteArrayInputStream(raw.toByteArray())
        val result = RequestDTO.parse(input)

        assertFalse(result.attributes["Malformed"] as? Boolean == true)
        assertEquals("/search?q=kotlin&page=2", result.target)
    }

    @Test
    fun malformed_attribute_is_absent_when_not_set() {
        val raw = "GET /ok HTTP/1.1\r\nHost: localhost\r\n\r\n"
        val input = ByteArrayInputStream(raw.toByteArray())
        val result = RequestDTO.parse(input)

        assertNull(result.attributes["Malformed"])
    }
}