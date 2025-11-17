package test

import io.voidx.dto.ok
import io.voidx.html.metadata.Metadata
import io.voidx.page.apiRoute
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
        meta.externalJS =
            linkedMapOf(
                "/js/first.js" to true, // deferred
                "/js/second.js" to false, // not deferred
            )

        val head = meta.render()

        // first.js should have defer
        assertTrue(head.contains("<script src=\"/js/first.js\" defer></script>"))

        // second.js should NOT have defer
        assertTrue(
            head.contains("<script src=\"/js/second.js\"></script>") ||
                head.contains("<script src=\"/js/second.js\" ></script>"),
        )

        // order preserved
        assertTrue(head.indexOf("first.js") < head.indexOf("second.js"))
    }
}
