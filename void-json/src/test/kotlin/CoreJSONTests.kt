package test

import io.voidx.dto.RequestDTO
import io.voidx.dto.buildRequest
import io.voidx.json.*
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CoreJSONTests {
    @Serializable
    data class Foo(
        val a: Int = 1,
        val b: String = "x",
    )

    class NotSerializable(
        val a: Int,
    )

    @Test
    fun json_roundtrip_default_and_pretty() {
        val foo = Foo(42, "answer")
        val json = foo.toJson().getOrThrow()
        val decoded = json.fromJson<Foo>().getOrThrow()
        assertEquals(foo, decoded)

        val prettyJson = foo.toJson(pretty = true).getOrThrow()
        val decodedPretty = prettyJson.fromJson<Foo>().getOrThrow()
        assertEquals(foo, decodedPretty)
        assertTrue(prettyJson.contains("\n"), "Pretty JSON should contain newlines")
    }

    @Test
    fun cbor_roundtrip_via_bytes_alias() {
        val foo = Foo(7, "seven")
        val bytes = foo.toBytes().getOrThrow()
        val decoded = bytes.fromJson<Foo>().getOrThrow()
        assertEquals(foo, decoded)
    }

    @Test
    fun protobuf_roundtrip_via_xml_alias() {
        val foo = Foo(9, "nine")
        val bytes = foo.toXml().getOrThrow()
        val decoded = bytes.fromXml<Foo>().getOrThrow()
        assertEquals(foo, decoded)
    }

    @Test
    fun base64_helpers() {
        val foo = Foo(3, "three")
        val b64 = foo.toJson64().getOrThrow()
        val decoded = b64.fromJson64<Foo>().getOrThrow()
        assertEquals(foo, decoded)
    }

    @Test
    fun detect_format_and_parse_body() {
        val bodyJson = Foo(11, "eleven").toJson().getOrThrow()
        val reqJson: RequestDTO =
            buildRequest {
                headers["Content-Type"] = "application/json"
                body = bodyJson
            }
        assertEquals(Format.JSON, reqJson.detectFormat())
        val parsed = reqJson.parseBody<Foo>().getOrThrow()
        assertEquals(11, parsed.a)
        assertEquals("eleven", parsed.b)

        val reqCbor: RequestDTO = buildRequest { headers["Content-Type"] = "application/cbor" }
        assertEquals(Format.CBOR, reqCbor.detectFormat())
        val reqXml: RequestDTO = buildRequest { headers["Content-Type"] = "application/xml" }
        assertEquals(Format.XML, reqXml.detectFormat())
        val reqText: RequestDTO = buildRequest { headers["Content-Type"] = "text/plain" }
        assertEquals(Format.TEXT, reqText.detectFormat())
    }

    @Test
    fun file_helpers_roundtrip() {
        val foo = Foo(21, "twenty-one")
        val dir = Files.createTempDirectory("foo-json-test")
        val path = dir.resolve("data.json") // does not exist yet
        try {
            foo.toJsonFile(pretty = true, path = path)
            val decoded = File(path.toUri()).fromJsonFile<Foo>().getOrThrow()
            assertEquals(foo, decoded)
        } finally {
            Files.deleteIfExists(path)
            Files.deleteIfExists(dir)
        }
    }

    @Test
    fun can_serialize_detection() {
        assertTrue(Foo().canSerialize())
        assertFalse(NotSerializable(1).canSerialize())
    }

    @Test
    fun detect_format_with_parameters_and_case() {
        val reqJsonWithParams: RequestDTO =
            buildRequest {
                headers["Content-Type"] = "Application/Json; charset=UTF-8"
            }
        assertEquals(Format.JSON, reqJsonWithParams.detectFormat())

        val reqVendorJson: RequestDTO =
            buildRequest {
                headers["Content-Type"] = "application/hal+json"
            }
        assertEquals(Format.JSON, reqVendorJson.detectFormat())

        val reqTextXml: RequestDTO = buildRequest { headers["Content-Type"] = "text/xml; charset=utf-8" }
        assertEquals(Format.XML, reqTextXml.detectFormat())

        val reqUnknown: RequestDTO = buildRequest { headers["Content-Type"] = "application/octet-stream" }
        assertEquals(Format.TEXT, reqUnknown.detectFormat())
    }

    @Test
    fun parse_body_invalid_json_returns_failure() {
        val req: RequestDTO =
            buildRequest {
                headers["Content-Type"] = "application/json"
                body = "{" // malformed JSON
            }
        val result = req.parseBody<Foo>()
        assertTrue(result.isFailure, "Expected failure when parsing invalid JSON body")
    }
}
