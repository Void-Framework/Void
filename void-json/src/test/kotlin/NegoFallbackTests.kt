package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.page.Page
import io.voidx.json.autoSerialize
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NegoFallbackTests {
    @Serializable
    data class Thing(
        val v: Int,
    )

    private fun pageWithAccept(accept: String): Page =
        object : Page("/p") {
            override fun content() = throw UnsupportedOperationException()
        }.apply {
            request = buildRequest { headers["Accept"] = accept }
        }

    @Test
    fun autoSerialize_falls_back_to_string_for_unknown_accept() {
        val page = pageWithAccept("application/custom")
        val resp = page.autoSerialize<Thing>(Thing(1))
        assertEquals("application/custom", resp.headers["Content-Type"]) // echo unknown Accept
        // Fallback path returns a String body via toString
        assertIs<ResponseBody.StringBody>(resp.body)
    }
}
