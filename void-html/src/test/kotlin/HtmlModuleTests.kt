package test

import io.voidx.html.Element
import io.voidx.html.metadata.Metadata
import io.voidx.html.util.createResponse
import io.voidx.util.HtmlIntegration
import io.voidx.html.router.RouterUtil // force object initialization
import io.voidx.generated.Div
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class HtmlModuleTests {
    @Test
    fun html_response_sets_headers_and_attributes() {
        val el = Div("id" to "root") { }
        val meta = Metadata(page = io.voidx.html.page.apiRoute("/") { createResponse(el) })

        val resp = createResponse(el, meta)
        assertEquals("text/html", resp.headers["Content-Type"])
        val attrElement = resp.attributes["Element"] as? Element
        assertNotNull(attrElement)
        assertEquals("div", attrElement.name)
        val body = when (val b = resp.body) {
            is io.voidx.dto.http.ResponseBody.StringBody -> b.body
            is io.voidx.dto.http.ResponseBody.ByteArrayBody -> String(b.body)
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
