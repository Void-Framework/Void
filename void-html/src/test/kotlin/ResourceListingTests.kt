package test

import io.voidx.router.listResourcePaths
import kotlin.test.Test
import kotlin.test.assertTrue

class ResourceListingTests {
    @Test
    fun list_resource_paths_enumerates_js_resources() {
        val paths = listResourcePaths("js")
        // Should find at least the bundled client script
        assertTrue(paths.isNotEmpty(), "Expected at least one JS resource path")
        assertTrue(paths.all { it.startsWith("js/") }, "All entries should be under js/")
        assertTrue(paths.any { it.endsWith(".js") }, "Should include .js files")
    }
}
