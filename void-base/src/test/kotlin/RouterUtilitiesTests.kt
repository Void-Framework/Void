package test

import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
}
