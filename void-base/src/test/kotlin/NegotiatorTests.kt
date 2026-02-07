package test

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.dto.ok
import io.voidx.json.JsonType
import io.voidx.json.Negotiator
import io.voidx.json.json
import org.junit.jupiter.api.Test
import kotlin.test.*

class NegotiatorTests {
    @Test
    fun `test Negotiator whenType returns block result when type matches`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.whenType(JsonType) {
            ok("matched")
        }

        assertNotNull(result)
        assertEquals("matched", result.body.body as String)
    }

    @Test
    fun `test Negotiator whenType returns null when type does not match`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "text/plain"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.whenType(JsonType) {
            ok("should not match")
        }

        assertNull(result)
    }

    @Test
    fun `test Negotiator or returns original response when not null`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        val negotiator = Negotiator(request)
        val original = ok("original")

        val result = original or {
            ok("fallback")
        }

        assertSame(original, result)
        assertEquals("original", result?.body?.body as String)
    }

    @Test
    fun `test Negotiator or returns fallback when original is null`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        val negotiator = Negotiator(request)
        val original: ResponseDTO? = null

        val result = negotiator.run {
            original or {
                ok("fallback")
            }
        }

        assertNotNull(result)
        assertEquals("fallback", result?.body?.body as String)
    }

    @Test
    fun `test Negotiator or can return null from fallback`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        val negotiator = Negotiator(request)
        val original: ResponseDTO? = null

        val result = negotiator.run {
            original or {
                null
            }
        }

        assertNull(result)
    }

    @Test
    fun `test JsonType matches with exact application json Content-Type`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        assertTrue(JsonType.matches(request))
    }

    @Test
    fun `test JsonType matches with application json and charset`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json; charset=utf-8"), "")
        assertTrue(JsonType.matches(request))
    }

    @Test
    fun `test JsonType does not match text plain`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "text/plain"), "")
        assertFalse(JsonType.matches(request))
    }

    @Test
    fun `test JsonType does not match when Content-Type is missing`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        assertFalse(JsonType.matches(request))
    }

    @Test
    fun `test JsonType does not match when Content-Type is null`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to ""), "")
        assertFalse(JsonType.matches(request))
    }

    @Test
    fun `test JsonType does not match application xml`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/xml"), "")
        assertFalse(JsonType.matches(request))
    }

    @Test
    fun `test JsonType contentType property`() {
        assertEquals("application/json", JsonType.contentType)
    }

    @Test
    fun `test json extension function returns response when Content-Type matches`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.json {
            ok("json response")
        }

        assertNotNull(result)
        assertEquals("json response", result?.body?.body as String)
    }

    @Test
    fun `test json extension function returns null when Content-Type does not match`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "text/html"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.json {
            ok("should not execute")
        }

        assertNull(result)
    }

    @Test
    fun `test custom NegotiateType can be created and used`() {
        object XmlType : Negotiator.NegotiateType() {
            override val contentType = "application/xml"
            override fun matches(request: RequestDTO) = request["Content-Type"]?.startsWith(contentType) == true
        }

        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/xml"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.whenType(XmlType) {
            ok("xml response")
        }

        assertNotNull(result)
        assertEquals("xml response", result?.body?.body as String)
    }

    @Test
    fun `test custom NegotiateType with complex matching logic`() {
        object ComplexType : Negotiator.NegotiateType() {
            override val contentType = "application/vnd.api+json"
            override fun matches(request: RequestDTO): Boolean {
                val ct = request["Content-Type"] ?: return false
                val accept = request["Accept"] ?: return false
                return ct.contains("application/json") && accept.contains("application/json")
            }
        }

        val request = RequestDTO(
            Method.GET,
            "/test",
            mutableMapOf(
                "Content-Type" to "application/json",
                "Accept" to "application/json"
            ),
            ""
        )
        val negotiator = Negotiator(request)

        val result = negotiator.whenType(ComplexType) {
            ok("complex match")
        }

        assertNotNull(result)
    }

    @Test
    fun `test Negotiator chaining with or for fallback pattern`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "text/plain"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.run {
            json { ok("json") } or {
                ok("fallback")
            }
        }

        assertNotNull(result)
        assertEquals("fallback", result?.body?.body as String)
    }

    @Test
    fun `test Negotiator multiple whenType checks in sequence`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        val negotiator = Negotiator(request)

        object XmlType : Negotiator.NegotiateType() {
            override val contentType = "application/xml"
            override fun matches(request: RequestDTO) = request["Content-Type"]?.startsWith(contentType) == true
        }

        val xmlResult = negotiator.whenType(XmlType) { ok("xml") }
        val jsonResult = negotiator.whenType(JsonType) { ok("json") }

        assertNull(xmlResult)
        assertNotNull(jsonResult)
        assertEquals("json", jsonResult?.body?.body as String)
    }

    @Test
    fun `test Negotiator whenType block is not executed when type does not match`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "text/plain"), "")
        val negotiator = Negotiator(request)
        var executed = false

        negotiator.whenType(JsonType) {
            executed = true
            ok("should not execute")
        }

        assertFalse(executed)
    }

    @Test
    fun `test Negotiator whenType block is executed when type matches`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        val negotiator = Negotiator(request)
        var executed = false

        negotiator.whenType(JsonType) {
            executed = true
            ok("executed")
        }

        assertTrue(executed)
    }

    @Test
    fun `test Negotiator or chain with multiple fallbacks`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        val negotiator = Negotiator(request)

        val result = negotiator.run {
            (null as ResponseDTO?) or {
                (null as ResponseDTO?) or {
                    ok("final fallback")
                }
            }
        }

        assertNotNull(result)
        assertEquals("final fallback", result?.body?.body as String)
    }

    @Test
    fun `test Negotiator request property is accessible`() {
        val request = RequestDTO(Method.POST, "/api/test", mutableMapOf("Custom-Header" to "value"), "body")
        val negotiator = Negotiator(request)

        assertSame(request, negotiator.request)
        assertEquals(Method.POST, negotiator.request.method)
        assertEquals("/api/test", negotiator.request.target)
        assertEquals("value", negotiator.request["Custom-Header"])
        assertEquals("body", negotiator.request.body)
    }

    @Test
    fun `test JsonType matches case sensitive content type`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "Application/JSON"), "")
        // Assuming the implementation is case-sensitive
        assertFalse(JsonType.matches(request))
    }

    @Test
    fun `test Negotiator or does not invoke fallback when original is present`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        val negotiator = Negotiator(request)
        val original = ok("original")
        var fallbackInvoked = false

        val result = negotiator.run {
            original or {
                fallbackInvoked = true
                ok("fallback")
            }
        }

        assertFalse(fallbackInvoked)
        assertSame(original, result)
    }

    @Test
    fun `test Negotiator whenType with buildResponse`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.whenType(JsonType) {
            buildResponse<String> {
                status = 201
                statusText = "Created"
                body = "resource created"
            }
        }

        assertNotNull(result)
        assertEquals(201, result?.status)
        assertEquals("Created", result?.statusText)
    }

    @Test
    fun `test json extension with complex response`() {
        val request = RequestDTO(Method.POST, "/test", mutableMapOf("Content-Type" to "application/json"), "{\"key\":\"value\"}")
        val negotiator = Negotiator(request)

        val result = negotiator.json {
            buildResponse<String> {
                status = 200
                statusText = "OK"
                headers["Custom-Header"] = "custom-value"
                body = "processed: ${request.body}"
            }
        }

        assertNotNull(result)
        assertEquals(200, result?.status)
        assertEquals("custom-value", result?.headers?.get("Custom-Header"))
        assertEquals("processed: {\"key\":\"value\"}", result?.body?.body as String)
    }

    @Test
    fun `test NegotiateType with empty Content-Type header`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to ""), "")
        assertFalse(JsonType.matches(request))
    }

    @Test
    fun `test Negotiator with multiple content negotiation patterns`() {
        val request = RequestDTO(Method.GET, "/test", mutableMapOf("Content-Type" to "application/json"), "")
        val negotiator = Negotiator(request)

        val result = negotiator.run {
            json { ok("json matched") } or {
                ok("default")
            }
        }

        assertEquals("json matched", result?.body?.body as String)
    }
}