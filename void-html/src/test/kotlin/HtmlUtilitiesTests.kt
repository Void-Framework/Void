package test

import io.voidx.generated.Div
import io.voidx.generated.H2
import io.voidx.html.Element
import io.voidx.html.metadata.Metadata
import io.voidx.html.page.apiRoute
import io.voidx.dto.http.ok
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HtmlUtilitiesTests {
    @Test
    fun element_findElement_matches_by_id_and_class_recursively() {
        val root = Div("id" to "root") {
            Div("class" to "box") {
                H2("id" to "title") { }
            }
        }

        val byId = root.findElement("#title")
        assertNotNull(byId)
        assertEquals("h2", byId.name)

        val byClass = root.findElement(".box")
        assertNotNull(byClass)
        assertEquals("div", byClass.name)
    }

    @Test
    fun metadata_render_includes_external_css_and_js_and_defaults() {
        val page = apiRoute("/") { ok("", mutableMapOf("Content-Type" to "text/plain")) }
        val meta = Metadata(page)
        meta.title = "Test Page"
        meta.externalCss = mutableListOf("/css/site.css")
        meta.externalJS = mutableMapOf("/js/app.js" to true)
        meta.rawTags += "<meta name=\"x-test\" content=\"1\">"

        val head = meta.render()
        assertTrue(head.contains("<title>Test Page</title>"))
        assertTrue(head.contains("<link rel=\"stylesheet\" href=\"/css/site.css\">"))
        assertTrue(head.contains("<script src=\"/js/app.js\" defer></script>"))
        assertTrue(head.contains("<meta name=\"x-test\" content=\"1\">"))
        // Robots default present
        assertTrue(head.contains("name=\"robots\""))
    }
}
