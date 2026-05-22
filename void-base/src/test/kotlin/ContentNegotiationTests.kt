package test

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.dto.headers
import io.voidx.json.autoSerialize
import io.voidx.page.Page
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ContentNegotiationTests {
    @Serializable
    data class Person(
        val name: String,
        val age: Int,
    )

    private fun dummyPage(): Page {
        return object : Page("/test") {
            override fun content(request: RequestDTO, queries: Map<String, String>) = throw UnsupportedOperationException()
        }
    }

    @Test
    fun autoSerialize_respects_accept_json_by_default() {
        val page = dummyPage()
        val req = buildRequest { } // default to application/json in autoSerialize
        val resp = page.autoSerialize<Person>(req, Person("Ada", 36))
        assertEquals("application/json", resp.headers["Content-Type"])
        val body = resp.body
        assertIs<ResponseBody.StringBody>(body)
        assertTrue(body.body.contains("\"name\":\"Ada\""))
    }

    @Test
    fun autoSerialize_respects_accept_header_variants() {
        val page = dummyPage()
        val reqJson = buildRequest { headers { put("Accept", "application/json") } }
        val rJson = page.autoSerialize<Person>(reqJson, Person("Bob", 40))
        assertEquals("application/json", rJson.headers["Content-Type"])

        val reqXml = buildRequest { headers { put("Accept", "application/xml") } }
        val rXml = page.autoSerialize<Person>(reqXml, Person("Bob", 40))
        assertEquals("application/xml", rXml.headers["Content-Type"])
        // For XML/ProtoBuf path we currently return bytes; ensure a body exists
        // Body type may be String or ByteArray depending on implementation; just check it's non-empty when string.
        when (val b = rXml.body) {
            is ResponseBody.StringBody -> assertTrue(b.body.isNotEmpty())
            is ResponseBody.ByteArrayBody -> assertTrue(b.body.isNotEmpty())
        }
    }
}
