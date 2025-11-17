package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.ResponseDTO
import io.voidx.dto.badRequest
import io.voidx.dto.fileDownload
import io.voidx.dto.guessContentType
import io.voidx.dto.permanentRedirect
import io.voidx.dto.redirect
import io.voidx.dto.temporaryRedirect
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResponseHelpersAndLegacyTests {
    @Test
    fun redirect_helpers_set_status_and_location() {
        val r1 = redirect("/to")
        assertEquals(302, r1.status)
        assertEquals("Found", r1.statusText)
        assertEquals("/to", r1.headers["Location"])

        val r2 = temporaryRedirect("/tmp")
        assertEquals(307, r2.status)
        assertEquals("Temporary Redirect", r2.statusText)
        assertEquals("/tmp", r2.headers["Location"])

        val r3 = permanentRedirect("/perma")
        assertEquals(308, r3.status)
        assertEquals("Permanent Redirect", r3.statusText)
        assertEquals("/perma", r3.headers["Location"])
    }

    @Test
    fun error_shortcuts_build_expected_statuses() {
        val r = badRequest("oops", mutableMapOf("Content-Type" to "text/plain"))
        assertEquals(400, r.status)
        assertEquals("Bad Request", r.statusText)
        val body = r.body
        assertTrue(body is ResponseBody.StringBody && body.body == "oops")
    }

    @Test
    fun fileDownload_sets_disposition_and_content_type() {
        val tmp =
            kotlin.io.path
                .createTempFile(suffix = ".txt")
                .toFile()
        tmp.writeText("hello")
        try {
            val resp = fileDownload(tmp)
            assertEquals("attachment; filename=\"${tmp.name}\"", resp.headers["Content-Disposition"])
            // guessContentType should detect text/plain for .txt
            assertEquals("text/plain", resp.headers["Content-Type"])
            val b = resp.body
            assertTrue(b is ResponseBody.ByteArrayBody && String(b.body).contains("hello"))
        } finally {
            tmp.delete()
        }
    }

    @Test
    fun guessContentType_fallbacks_and_specific_types() {
        // Known
        assertEquals("text/plain", guessContentType(File("a.txt")))
        // Unknown should fallback to application/octet-stream
        assertEquals("application/octet-stream", guessContentType(File("a.unknownext")))
    }
}
