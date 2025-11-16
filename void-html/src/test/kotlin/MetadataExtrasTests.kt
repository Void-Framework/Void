package test

import io.voidx.dto.http.ok
import io.voidx.html.page.apiRoute
import io.voidx.html.metadata.Metadata
import kotlin.test.Test
import kotlin.test.assertTrue

class MetadataExtrasTests {
    @Test
    fun favicon_and_keywords_and_canonical_render() {
        val page = apiRoute("/") { ok("", mutableMapOf("Content-Type" to "text/plain")) }
        val meta = Metadata(page)
        meta.favicon = "/favicon.ico" to "image/x-icon"
        meta.keywords = listOf("kotlin", "void")
        meta.canonical = "https://example.com/"

        val head = meta.render()
        assertTrue(head.contains("<link rel=\"icon\" href=\"/favicon.ico\" type=\"image/x-icon\">"))
        assertTrue(head.contains("name=\"keywords\""))
        assertTrue(head.contains("<link rel=\"canonical\" href=\"https://example.com/\">"))
    }

    @Test
    fun external_js_defer_false_is_rendered_without_defer_and_order_preserved() {
        val page = apiRoute("/") { ok("", mutableMapOf("Content-Type" to "text/plain")) }
        val meta = Metadata(page)
        meta.externalJS = linkedMapOf(
            "/js/first.js" to true, // deferred
            "/js/second.js" to false, // not deferred
        )

        val head = meta.render()
        // The second script should not include the defer attribute
        assertTrue(head.contains("<script src=\"/js/first.js\" defer></script>"))
        assertTrue(head.contains("<script src=\"/js/second.js\" "></script>") || head.contains("<script src=\"/js/second.js\" ></script>"))
        // And order is preserved (first.js appears before second.js)
        assertTrue(head.indexOf("first.js") < head.indexOf("second.js"))
    }
}
