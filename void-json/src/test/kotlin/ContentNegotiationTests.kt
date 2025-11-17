package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.page.Page
import io.voidx.json.autoSerialize
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

    private fun dummyPage(accept: String?): Page {
        val p =
            object : Page("/test") {
                override fun content() = throw UnsupportedOperationException()
            }
        p.request =
            buildRequest {
                if (accept != null) headers["Accept"] = accept
            }
        return p
    }

    @Test
    fun autoSerialize_respects_accept_json_by_default() {
        val page = dummyPage(null) // default to application/json
        val resp = page.autoSerialize<Person>(Person("Ada", 36))
        assertEquals("application/json", resp.headers["Content-Type"])
        val body = resp.body
        assertIs<ResponseBody.StringBody>(body)
        assertTrue(body.body.contains("\"name\":\"Ada\""))
    }

    @Test
    fun autoSerialize_respects_accept_header_variants() {
        val pageJson = dummyPage("application/json")
        val rJson = pageJson.autoSerialize<Person>(Person("Bob", 40))
        assertEquals("application/json", rJson.headers["Content-Type"])

        val pageXml = dummyPage("application/xml")
        val rXml = pageXml.autoSerialize<Person>(Person("Bob", 40))
        assertEquals("application/xml", rXml.headers["Content-Type"])
        // For XML/ProtoBuf path we currently return bytes; ensure a body exists
        // Body type may be String or ByteArray depending on implementation; just check it's non-empty when string.
        when (val b = rXml.body) {
            is ResponseBody.StringBody -> assertTrue(b.body.isNotEmpty())
            is ResponseBody.ByteArrayBody -> assertTrue(b.body.isNotEmpty())
        }
    }
}
