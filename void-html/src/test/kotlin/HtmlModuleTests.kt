package test

import io.voidx.dto.ResponseBody
import io.voidx.dto.ok
import io.voidx.html.Element
import io.voidx.html.fractal
import io.voidx.html.generated.Div
import io.voidx.html.metadata.Metadata
import io.voidx.html.router.RouterUtil // force object initialization
import io.voidx.html.util.createResponse
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.util.HtmlIntegration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HtmlModuleTests {
    @Test
    fun html_response_sets_headers_and_attributes() {
        val el = fractal { Div("id" to "root") { } }
        val meta = Metadata(route("/") {
            GET {
                createResponse(el)
            }
        })

        val resp = createResponse(el, meta)
        assertEquals("text/html", resp.headers["Content-Type"])
        val attrElement = resp.attributes["Element"] as? Element
        assertNotNull(attrElement)
        assertEquals("", attrElement.name)
        val body =
            when (val b = resp.body) {
                is ResponseBody.StringBody -> b.body
                is ResponseBody.ByteArrayBody -> String(b.body)
            }
        assertTrue(body.contains("<head>"))
        assertTrue(body.contains("<body>"))
        assertTrue(body.contains("<div"))
    }

    @Test
    fun router_util_initializes_html_integration_hooks() {
        // Accessing RouterUtil ensures its init block runs (ModuleInit registry)
        val ensureInit = RouterUtil
        // Hooks should be set by RouterUtil.init()
        assertNotNull(HtmlIntegration.handleJsAndCss)
    }
}
