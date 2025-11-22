package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.ok
import io.voidx.html.Element
import io.voidx.html.fractal
import io.voidx.html.generated.Div
import io.voidx.html.generated.H2
import io.voidx.html.metadata.Metadata
import io.voidx.html.util.createResponse
import io.voidx.page.Page
import io.voidx.page.route
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HtmlUtilitiesTests {
    @Test
    fun element_findElement_matches_by_id_and_class_recursively() {
        val root =
            fractal {
                Div("id" to "root") {
                    Div("class" to "box") {
                        H2("id" to "title") { }
                    }
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
        val page = route("/") {
            GET {
                ok("", mutableMapOf("Content-Type" to "text/plain"))
            }
        }
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

    @Test
    fun createResponse_overload_sets_headers_and_element_attribute() {
        val el = fractal { Div("id" to "only") { } }
        val resp = createResponse(el)
        // Header
        assertEquals("text/html", resp.headers["Content-Type"])
        // Attributes should include original element
        val stored = resp.attributes["Element"] as? Element
        assertNotNull(stored)
        assertEquals("", stored.name)
        // Body is the element's render() output
        val body =
            when (val b = resp.body) {
                is ResponseBody.StringBody -> b.body
                is ResponseBody.ByteArrayBody -> String(b.body)
            }
        assertTrue(body.contains("<div"))
    }

    @Test
    fun metadata_style_uuid_injects_css_link() {
        val page = route("/") {
            GET {
                ok("", mutableMapOf("Content-Type" to "text/plain"))
            }
        }
        val meta = Metadata(page)
        val id = java.util.UUID.randomUUID()
        // internal property is visible within module tests
        meta.style = id
        val head = meta.render()
        assertTrue(head.contains("/css/$id/styles.css"))
    }
}
