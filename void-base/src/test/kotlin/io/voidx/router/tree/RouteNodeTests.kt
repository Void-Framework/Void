package io.voidx.router.tree

import io.voidx.dto.ok
import io.voidx.page.route
import io.voidx.router.exceptions.RouteTargetUsedException
import io.voidx.router.tree.RouteNode.Companion.segmentsOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RouteNodeTests {
    @Test
    fun insert_and_match_root_route() {
        val root = RouteNode()
        val p = route("/") { GET { _, _ -> ok("root") } }

        root.insert(segmentsOf("/"), 0, p)

        val params = mutableMapOf<String, String>()
        val matched = root.match(segmentsOf("/"), 0, params)
        assertEquals(p, matched)
        assertTrue(params.isEmpty())
    }

    @Test
    fun insert_duplicate_root_route_throws() {
        val root = RouteNode()
        val p1 = route("/") { GET { _, _ -> ok("1") } }
        val p2 = route("/") { GET { _, _ -> ok("2") } }

        root.insert(segmentsOf("/"), 0, p1)
        assertFailsWith<RouteTargetUsedException> {
            root.insert(segmentsOf("/"), 0, p2)
        }
    }

    @Test
    fun segments_of_splits_paths_correctly() {
        assertEquals(emptyList(), segmentsOf("/"))
        assertEquals(emptyList(), segmentsOf(""))
        assertEquals(emptyList(), segmentsOf("///"))
        assertEquals(listOf("a", "b", "c"), segmentsOf("/a/b/c"))
        assertEquals(listOf("a", "b", "c"), segmentsOf("/a/b/c/"))
        assertEquals(listOf("user", "{id}"), segmentsOf("/user/{id}"))
    }

    @Test
    fun insert_and_match_routes_with_segmentsOf() {
        val root = RouteNode()
        val pRoot = route("/") { GET { _, _ -> ok("root") } }
        val p1 = route("/a/b/c") { GET { _, _ -> ok("abc") } }
        val pDynamic = route("/user/{id}") { GET { _, _ -> ok("user") } }
        val pOptional = route("/blog/{slug?}") { GET { _, _ -> ok("blog") } }

        root.insert(segmentsOf("/"), 0, pRoot)
        root.insert(segmentsOf("/a/b/c"), 0, p1)
        root.insert(segmentsOf("/user/{id}"), 0, pDynamic)
        root.insert(segmentsOf("/blog/{slug?}"), 0, pOptional)

        var params = mutableMapOf<String, String>()
        assertEquals(pRoot, root.match(segmentsOf("/"), 0, params))
        assertTrue(params.isEmpty())

        params = mutableMapOf()
        assertEquals(p1, root.match(segmentsOf("/a/b/c"), 0, params))

        params = mutableMapOf()
        assertEquals(pDynamic, root.match(segmentsOf("/user/123"), 0, params))
        assertEquals("123", params["id"])

        params = mutableMapOf()
        assertEquals(pOptional, root.match(segmentsOf("/blog/news"), 0, params))
        assertEquals("news", params["slug"])

        params = mutableMapOf()
        assertEquals(pOptional, root.match(segmentsOf("/blog"), 0, params))
        assertEquals("", params["slug"])

        assertNull(root.match(segmentsOf("/unknown"), 0, mutableMapOf()))
    }

    @Test
    fun insert_and_match_static_routes() {
        val root = RouteNode()
        val p1 = route("/a/b/c") { GET { _, _ -> ok("abc") } }
        val p2 = route("/a/b/d") { GET { _, _ -> ok("abd") } }

        root.insert("/a/b/c".split("/"), 1, p1)
        root.insert("/a/b/d".split("/"), 1, p2)

        val params = mutableMapOf<String, String>()
        assertEquals(p1, root.match("/a/b/c".split("/"), 1, params))
        assertEquals(p2, root.match("/a/b/d".split("/"), 1, params))
        assertNull(root.match("/a/b".split("/"), 1, params))
        assertNull(root.match("/a/b/e".split("/"), 1, params))
    }

    @Test
    fun insert_and_match_dynamic_routes() {
        val root = RouteNode()
        val p = route("/user/{id}") { GET { _, _ -> ok("user") } }

        root.insert("/user/{id}".split("/"), 1, p)

        val params = mutableMapOf<String, String>()
        val matched = root.match("/user/42".split("/"), 1, params)
        assertEquals(p, matched)
        assertEquals("42", params["id"])
    }

    @Test
    fun insert_and_match_multiple_dynamic_segments() {
        val root = RouteNode()
        val p = route("/blog/{year}/{month}/{day}") { GET { _, _ -> ok("blog") } }

        root.insert("/blog/{year}/{month}/{day}".split("/"), 1, p)

        val params = mutableMapOf<String, String>()
        val matched = root.match("/blog/2026/05/22".split("/"), 1, params)
        assertEquals(p, matched)
        assertEquals("2026", params["year"])
        assertEquals("05", params["month"])
        assertEquals("22", params["day"])
    }

    @Test
    fun insert_duplicate_static_route_throws() {
        val root = RouteNode()
        val p1 = route("/a") { GET { _, _ -> ok("1") } }
        val p2 = route("/a") { GET { _, _ -> ok("2") } }

        root.insert("/a".split("/"), 1, p1)
        assertFailsWith<RouteTargetUsedException> {
            root.insert("/a".split("/"), 1, p2)
        }
    }

    @Test
    fun static_takes_precedence_over_dynamic() {
        val root = RouteNode()
        val pStatic = route("/user/me") { GET { _, _ -> ok("me") } }
        val pDynamic = route("/user/{id}") { GET { _, _ -> ok("id") } }

        root.insert("/user/me".split("/"), 1, pStatic)
        root.insert("/user/{id}".split("/"), 1, pDynamic)

        val params = mutableMapOf<String, String>()
        assertEquals(pStatic, root.match("/user/me".split("/"), 1, params))
        assertEquals(pDynamic, root.match("/user/other".split("/"), 1, params))
        assertEquals("other", params["id"])
    }

    @Test
    fun insert_and_match_optional_dynamic_routes() {
        val root = RouteNode()
        val p = route("/blog/{slug?}") { GET { _, _ -> ok("blog") } }

        root.insert("/blog/{slug?}".split("/"), 1, p)

        val params = mutableMapOf<String, String>()
        // Match with value
        val matchedWith = root.match("/blog/hello".split("/"), 1, params)
        assertEquals(p, matchedWith)
        assertEquals("hello", params["slug"])

        // Match without value
        params.clear()
        val matchedWithout = root.match("/blog".split("/"), 1, params)
        assertEquals(p, matchedWithout)
        assertEquals("", params["slug"])
    }

    @Test
    fun complex_mixed_routing() {
        val root = RouteNode()
        val p1 = route("/a/{b}/c") { GET { _, _ -> ok("1") } }
        val p2 = route("/a/{b}/{d?}") { GET { _, _ -> ok("2") } }

        root.insert("/a/{b}/c".split("/"), 1, p1)
        root.insert("/a/{b}/{d?}".split("/"), 1, p2)

        var params = mutableMapOf<String, String>()
        assertEquals(p1, root.match("/a/valB/c".split("/"), 1, params))
        assertEquals("valB", params["b"])

        params = mutableMapOf()
        assertEquals(p2, root.match("/a/valB/other".split("/"), 1, params))
        assertEquals("valB", params["b"])
        assertEquals("other", params["d"])

        params = mutableMapOf()
        assertEquals(p2, root.match("/a/valB".split("/"), 1, params))
        assertEquals("valB", params["b"])
        assertEquals("", params["d"])
    }
}
