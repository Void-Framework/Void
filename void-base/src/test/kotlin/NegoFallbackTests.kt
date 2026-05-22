package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.dto.headers
import io.voidx.json.autoSerialize
import io.voidx.page.Page
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NegoFallbackTests {
    @Serializable
    data class Thing(
        val v: Int,
    )

    private fun pageWithAccept(): Page =
        object : Page("/p") {
            override fun content(
                request: io.voidx.dto.RequestDTO,
                queries: Map<String, String>,
            ) = throw UnsupportedOperationException()
        }

    @Test
    fun autoSerialize_falls_back_to_string_for_unknown_accept() {
        val page = pageWithAccept()
        val req = buildRequest { headers { put("Accept", "application/custom") } }
        val resp = page.autoSerialize<Thing>(req, Thing(1))
        assertEquals("application/custom", resp.headers["Content-Type"]) // echo unknown Accept
        // Fallback path returns a String body via toString
        assertIs<ResponseBody.StringBody>(resp.body)
    }
}
