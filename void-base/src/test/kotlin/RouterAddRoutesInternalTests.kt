package test

import io.voidx.dto.ok
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.router.Router
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class RouterAddRoutesInternalTests {
    @Test
    fun addRoutes_registers_all_pages_and_returns_router() {
        val r = Router()
        val p1: Page = route("/a") { GET { _, _ -> ok("a", mutableMapOf("Content-Type" to "text/plain")) } }
        val p2: Page = route("/b") { GET { _, _ -> ok("b", mutableMapOf("Content-Type" to "text/plain")) } }
        val p3: Page = route("/c") { GET { _, _ -> ok("c", mutableMapOf("Content-Type" to "text/plain")) } }

        val returned = r.addRoutes(listOf(p1, p2, p3))
        assertEquals(r, returned)
        val params = mutableMapOf<String, String>()
        assertNotNull(r.rootNode.match("/a".split("/"), 1, params))
        assertNotNull(r.rootNode.match("/b".split("/"), 1, params))
        assertNotNull(r.rootNode.match("/c".split("/"), 1, params))
    }
}
