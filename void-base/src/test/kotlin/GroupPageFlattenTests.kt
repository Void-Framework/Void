package io.voidx.page

import io.voidx.dto.ok
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the changed GroupPage.flatten() method introduced in this PR.
 *
 * Covers:
 * - flatten() sets flattened=true on the root GroupPage
 * - flatten() sets flattened=true on all nested GroupPage instances
 * - flatten() returns a depth-first ordered list starting with the root
 * - flatten() returns non-GroupPage leaf handlers as plain PageHandler entries
 * - Single-level group (no children)
 * - Multiple levels of nesting
 */
class GroupPageFlattenTests {

    @Test
    fun flatten_sets_flattened_true_on_root() {
        val root = GroupPage("/api")
        assertFalse(root.flattened, "flattened should be false before flatten()")

        root.flatten()

        assertTrue(root.flattened, "flattened should be true after flatten()")
    }

    @Test
    fun flatten_returns_root_in_list() {
        val root = GroupPage("/api")
        val pages = root.flatten()

        assertTrue(pages.isNotEmpty())
        assertEquals(root, pages[0])
    }

    @Test
    fun flatten_single_group_with_no_children_returns_only_root() {
        val root = GroupPage("/root")
        val pages = root.flatten()

        assertEquals(1, pages.size)
        assertEquals(root, pages[0])
    }

    @Test
    fun flatten_sets_flattened_true_on_all_nested_group_pages() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    group("/items") {
                        GET { _, _ -> ok("items") }
                    }
                }
                group("/v2") {
                    GET { _, _ -> ok("v2") }
                }
            }

        // Before flatten, no group is marked flattened
        assertFalse(root.flattened)
        root.routes.filterIsInstance<GroupPage>().forEach { child ->
            assertFalse(child.flattened, "Child ${child.target} should not be flattened yet")
        }

        root.flatten()

        assertTrue(root.flattened, "Root should be flattened")
        // All nested GroupPages should be marked flattened
        fun checkFlattened(gp: GroupPage) {
            assertTrue(gp.flattened, "GroupPage ${gp.target} should be flattened")
            gp.routes.filterIsInstance<GroupPage>().forEach { checkFlattened(it) }
        }
        checkFlattened(root)
    }

    @Test
    fun flatten_returns_depth_first_list_with_root_first() {
        val root =
            groupRoute("/a") {
                group("/b") {
                    group("/c") {
                        GET { _, _ -> ok("c") }
                    }
                }
                group("/d") {
                    GET { _, _ -> ok("d") }
                }
            }

        val pages = root.flatten()

        // Root should be first
        assertEquals("/a", pages[0].target)

        // "/a/b" should come before "/a/d" (depth-first)
        val bIndex = pages.indexOfFirst { it.target == "/a/b" }
        val cIndex = pages.indexOfFirst { it.target == "/a/b/c" }
        val dIndex = pages.indexOfFirst { it.target == "/a/d" }

        assertTrue(bIndex >= 0, "/a/b should be in flattened list")
        assertTrue(cIndex >= 0, "/a/b/c should be in flattened list")
        assertTrue(dIndex >= 0, "/a/d should be in flattened list")
        assertTrue(bIndex < cIndex, "/a/b should appear before /a/b/c (depth-first)")
        assertTrue(cIndex < dIndex, "/a/b/c should appear before /a/d (depth-first)")
    }

    @Test
    fun flatten_result_contains_all_nested_page_handlers() {
        val root =
            groupRoute("/top") {
                group("/g1") {
                    GET { _, _ -> ok("g1") }
                }
                group("/g2") {
                    group("/g3") {
                        GET { _, _ -> ok("g3") }
                    }
                }
            }

        val pages = root.flatten()
        val targets = pages.map { it.target }

        assertTrue("/top" in targets, "/top should be in flattened list")
        assertTrue("/top/g1" in targets, "/top/g1 should be in flattened list")
        assertTrue("/top/g2" in targets, "/top/g2 should be in flattened list")
        assertTrue("/top/g2/g3" in targets, "/top/g2/g3 should be in flattened list")
        assertEquals(4, pages.size, "Should have exactly 4 pages in flattened list")
    }

    @Test
    fun flatten_called_twice_sets_flattened_consistently() {
        val root = groupRoute("/x") { group("/y") { GET { _, _ -> ok("y") } } }

        root.flatten()
        assertTrue(root.flattened)

        // Calling flatten again should still work (no exception)
        val pages = root.flatten()
        assertTrue(root.flattened)
        assertTrue(pages.isNotEmpty())
    }

    @Test
    fun flatten_deeply_nested_groups_all_marked_flattened() {
        val root =
            groupRoute("/level1") {
                group("/level2") {
                    group("/level3") {
                        group("/level4") {
                            GET { _, _ -> ok("deep") }
                        }
                    }
                }
            }

        val pages = root.flatten()

        val targets = pages.map { it.target }
        assertTrue("/level1" in targets)
        assertTrue("/level1/level2" in targets)
        assertTrue("/level1/level2/level3" in targets)
        assertTrue("/level1/level2/level3/level4" in targets)
        assertEquals(4, pages.size)

        // All GroupPage instances in result should be flattened
        pages.filterIsInstance<GroupPage>().forEach { gp ->
            assertTrue(gp.flattened, "GroupPage ${gp.target} should have flattened=true")
        }
    }

    @Test
    fun flatten_on_empty_nested_group_returns_just_root_and_child() {
        val root =
            groupRoute("/root") {
                group("/child") {
                    // no handler, just an empty group
                }
            }

        val pages = root.flatten()

        assertEquals(2, pages.size)
        assertEquals("/root", pages[0].target)
        assertEquals("/root/child", pages[1].target)
        assertTrue(pages[0].flattened, "/root GroupPage should be flattened")
        assertTrue((pages[1] as GroupPage).flattened, "/root/child GroupPage should be flattened")
    }
}