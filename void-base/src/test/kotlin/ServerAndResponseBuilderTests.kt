package test

import io.voidx.Server
import io.voidx.dto.buildResponse
import io.voidx.router.Router
import io.voidx.server
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerAndResponseBuilderTests {
    @Test
    fun server_builder_dsl_with_auto_start_false_builds_without_starting_sockets() {
        val r = Router()
        val s: Server =
            server {
                router = r
                httpVersion = 2.0
                autoStart = false // crucial: do not open sockets in tests
            }

        // Ensure configuration propagated
        assertEquals(2.0, s.httpVersion)
    }

    @Test
    fun build_response_unsupported_type_throws_illegal_argument() {
        assertFailsWith<IllegalArgumentException> {
            buildResponse<Int> { }
        }
    }

    @Test
    fun build_response_string_and_byte_array() {
        val resp1 =
            buildResponse<String> {
                status = 201
                statusText = "Created"
                body = "Done"
            }
        assertEquals(201, resp1.status)
        assertEquals("Created", resp1.statusText)
        assertEquals("Done", resp1.body.body)

        val bytes = byteArrayOf(1, 2, 3)
        val resp2 =
            buildResponse<ByteArray> {
                body = bytes
            }
        assertEquals(200, resp2.status)
        assertEquals(bytes, resp2.body.body)
    }
}
