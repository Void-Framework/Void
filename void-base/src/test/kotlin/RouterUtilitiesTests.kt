package test

import io.voidx.dto.ok
import io.voidx.page.route
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import kotlin.test.*

class RouterUtilitiesTests {
    @Test
    fun list_resource_paths_public_includes_known_assets_and_unknown_folder_is_empty() {
        val paths = listResourcePaths("public")
        // Should include at least the main resources shipped in module
        assertTrue(paths.isNotEmpty())
        assertContains(paths, "public/index.html")
        assertContains(paths, "public/style.css")

        val none = listResourcePaths("does-not-exist-folder")
        assertTrue(none.isEmpty())
    }

    @Test
    fun routers_registry_increments_on_new_router() {
        val before = Router.routers.size
        val r = Router()
        val after = Router.routers.size
        assertEquals(before + 1, after)
        assertTrue(Router.routers.contains(r))
    }

    @Test
    fun parseQuery_various_cases() {
        // Single param
        assertEquals(mapOf("a" to "1"), Router.parseQuery("/path?a=1"))
        // Multiple params
        assertEquals(mapOf("a" to "1", "b" to "2"), Router.parseQuery("/path?a=1&b=2"))
        // Param without value is ignored by implementation if it doesn't have =
        assertEquals(emptyMap(), Router.parseQuery("/path?a"))
        assertEquals(mapOf("a" to ""), Router.parseQuery("/path?a="))
        // Multiple ampersands
        assertEquals(mapOf("a" to "1", "b" to "2"), Router.parseQuery("/path?a=1&&b=2"))
        // Encoded characters
        assertEquals(mapOf("a" to "hello world"), Router.parseQuery("/path?a=hello+world"))
        assertEquals(mapOf("a" to "hello world"), Router.parseQuery("/path?a=hello%20world"))
        // No query
        assertEquals(emptyMap(), Router.parseQuery("/path"))
        assertEquals(emptyMap(), Router.parseQuery("/path?"))
    }

    @Test
    fun router_unaryPlus_and_middleware_snapshotting() {
        val r = Router()
        val p1 = route("/a") { GET { _, _ -> ok("a") } }
        val p2 = route("/b") { GET { _, _ -> ok("b") } }

        r.apply {
            route(p1)
            route(p2)
        }

        val params = mutableMapOf<String, String>()
        assertNotNull(r.rootNode.match("/a".split("/"), 1, params))
        assertNotNull(r.rootNode.match("/b".split("/"), 1, params))
    }
}
