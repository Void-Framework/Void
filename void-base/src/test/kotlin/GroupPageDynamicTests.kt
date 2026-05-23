package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.ResponseDTO
import io.voidx.dto.ok
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.router.Router
import io.voidx.router.exceptions.RouteNoTargetException
import io.voidx.router.router
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupPageDynamicTests {
    private fun Router.dispatch(req: RequestDTO): ResponseDTO {
        val rawTarget = req.target.removeSuffix(if (req.target.last() == '/') "/" else "")
        val qMark = rawTarget.indexOf('?')
        val target = if (qMark >= 0) rawTarget.take(qMark) else rawTarget
        val query = Router.parseQuery(rawTarget)
        val pathParams = mutableMapOf<String, String>()

        return rootNode
            .match(target.split("/"), 1, pathParams)
            ?.content(req, query + pathParams)
            ?: nullPage.content(req, query + pathParams)
    }

    @Test
    fun `test dynamic segments in group paths`() {
        val root =
            groupRoute("/users") {
                group("/{userId}") {
                    GET { _, query ->
                        val userId = query["userId"]
                        ok("User ID: $userId")
                    }
                    group("/posts") {
                        GET { _, query ->
                            val userId = query["userId"]
                            ok("Posts for user: $userId")
                        }
                    }
                }
            }
        val r = router { addRoute(root) }

        // Test /users/123
        val req = RequestDTO(Method.GET, "/users/123", mutableMapOf(), "")
        val resp1 = r.dispatch(req)
        assertEquals(200, resp1.status)
        assertEquals("User ID: 123", (resp1.body as ResponseBody.StringBody).body)

        // Test /users/123/posts
        val req2 = RequestDTO(Method.GET, "/users/123/posts", mutableMapOf(), "")
        val resp2 = r.dispatch(req2)
        assertEquals(200, resp2.status)
        assertEquals("Posts for user: 123", (resp2.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test multiple dynamic segments in hierarchy`() {
        val root =
            groupRoute("/org") {
                group("/{orgId}") {
                    group("/projects") {
                        group("/{projectId}") {
                            GET { _, query ->
                                val orgId = query["orgId"]
                                val projectId = query["projectId"]
                                ok("Org: $orgId, Project: $projectId")
                            }
                        }
                    }
                }
            }

        val req = RequestDTO(Method.GET, "/org/voidx/projects/1", mutableMapOf(), "")
        val r = router { addRoute(root) }
        val resp = r.dispatch(req)
        assertEquals(200, resp.status)
        assertEquals("Org: voidx, Project: 1", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test optional segment in group path present`() {
        val root =
            groupRoute("/docs") {
                group("/{section}/{page?}") {
                    GET { _, query ->
                        val section = query["section"]
                        val page = query["page"]
                        ok("Section: $section, Page: ${page ?: "index"}")
                    }
                }
            }
        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/docs/api/overview", mutableMapOf(), "")
        val resp = r.dispatch(req)
        assertEquals(200, resp.status)
        assertEquals("Section: api, Page: overview", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test optional segment in group path missing`() {
        val root =
            groupRoute("/docs") {
                group("/{section}/{page?}") {
                    GET { _, query ->
                        val section = query["section"]
                        val page = query["page?"]
                        ok("Section: $section, Page: ${page ?: "index"}")
                    }
                }
            }

        val req = RequestDTO(Method.GET, "/docs/api", mutableMapOf(), "")
        val r = router { addRoute(root) }
        val resp = r.dispatch(req)
        assertEquals(200, resp.status)
        assertEquals("Section: api, Page: index", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test middleware inheritance in nested groups`() {
        var parentBeforeCalled = false
        var childBeforeCalled = false

        val root =
            groupRoute("/api") {
                before(
                    relayBefore {
                        parentBeforeCalled = true
                        null
                    },
                )

                group("/v1") {
                    before(
                        relayBefore {
                            childBeforeCalled = true
                            null
                        },
                    )

                    GET { _, _ -> ok("v1") }
                }
            }

        val req = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.flatten().last().middlewareProcessBefore(req)
        root.content(req, emptyMap())

        // Both middleware should be called due to inheritance
        assert(parentBeforeCalled) { "Parent middleware should be called" }
        assert(childBeforeCalled) { "Child middleware should be called" }
    }

    @Test
    fun `test middleware short circuit in group`() {
        val root =
            groupRoute("/protected") {
                group("/{id}") {
                    before(
                        relayBefore {
                            ok("blocked")
                        },
                    )

                    GET { _, _ -> ok("should not reach") }
                }
            }

        val req = RequestDTO(Method.GET, "/protected/123", mutableMapOf(), "")
        val r = router { addRoute(root) }
        val resp = root.flatten().last().middlewareProcessBefore(req) ?: r.dispatch(req)
        assertEquals("blocked", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with trailing slash handling`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { _, _ -> ok("users") }
                }
            }
        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/api/users/", mutableMapOf(), "")
        val resp = r.dispatch(req)
        assertEquals(200, resp.status)
        assertEquals("users", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test deeply nested groups with dynamic segments`() {
        val root =
            groupRoute("/a") {
                group("/{b}") {
                    group("/c") {
                        group("/{d}") {
                            group("/e") {
                                GET { _, query ->
                                    val b = query["b"]
                                    val d = query["d"]
                                    ok("b=$b, d=$d")
                                }
                            }
                        }
                    }
                }
            }

        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/a/b-val/c/d-val/e", mutableMapOf(), "")
        val resp = r.dispatch(req)
        assertEquals(200, resp.status)
        assertEquals("b=b-val, d=d-val", (resp.body as ResponseBody.StringBody).body)
    }

    /*@Test
    fun `test group returns 404 for unmatched path`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { _, _ ->  ok("users") }
                }
            }

        val req = RequestDTO(Method.GET, "/api/products", mutableMapOf(), "")
        val r = router { addRoute(root) }
        val resp = r.dispatch(req)
        assertEquals(404, resp.status)
        assertNotNull(resp)
    }
    TODO: Broken RouteNode handling of PageHandler
     */

    @Test
    fun `test group with multiple methods on same path`() {
        val root =
            groupRoute("/api") {
                group("/resource") {
                    GET { _, _ -> ok("get resource") }
                    POST { _, _ -> ok("create resource") }
                    PUT { _, _ -> ok("update resource") }
                    DELETE { _, _ -> ok("delete resource") }
                }
            }

        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/api/resource", mutableMapOf(), "")
        assertEquals("get resource", (r.dispatch(req).body as ResponseBody.StringBody).body)

        val req2 = RequestDTO(Method.POST, "/api/resource", mutableMapOf(), "")
        assertEquals("create resource", (r.dispatch(req2).body as ResponseBody.StringBody).body)

        val req3 = RequestDTO(Method.PUT, "/api/resource", mutableMapOf(), "")
        assertEquals("update resource", (r.dispatch(req3).body as ResponseBody.StringBody).body)

        val req4 = RequestDTO(Method.DELETE, "/api/resource", mutableMapOf(), "")
        assertEquals("delete resource", (r.dispatch(req4).body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with sibling routes`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { _, _ -> ok("users") }
                }
                group("/posts") {
                    GET { _, _ -> ok("posts") }
                }
                group("/comments") {
                    GET { _, _ -> ok("comments") }
                }
            }

        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/api/users", mutableMapOf(), "")
        assertEquals("users", (r.dispatch(req).body as ResponseBody.StringBody).body)

        val req2 = RequestDTO(Method.GET, "/api/posts", mutableMapOf(), "")
        assertEquals("posts", (r.dispatch(req2).body as ResponseBody.StringBody).body)

        val req3 = RequestDTO(Method.GET, "/api/comments", mutableMapOf(), "")
        assertEquals("comments", (r.dispatch(req3).body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group dynamic segment with special characters`() {
        val root =
            groupRoute("/files") {
                group("/{filename}") {
                    GET { _, query ->
                        val filename = query["filename"]
                        ok("File: $filename")
                    }
                }
            }
        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/files/my-file.txt", mutableMapOf(), "")
        val resp = r.dispatch(req)
        assertEquals("File: my-file.txt", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with mixed static and dynamic paths`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { _, _ -> ok("all users") }

                    group("/{id}") {
                        GET { _, query ->
                            val id = query["id"]
                            ok("user $id")
                        }
                    }
                }
            }

        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/api/users", mutableMapOf(), "")
        assertEquals("all users", (r.dispatch(req).body as ResponseBody.StringBody).body)

        val req2 = RequestDTO(Method.GET, "/api/users/42", mutableMapOf(), "")
        assertEquals("user 42", (r.dispatch(req2).body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group request propagation to nested routes`() {
        var capturedMethod: Method? = null
        var capturedTarget: String? = null

        val root =
            groupRoute("/api") {
                group("/{id}") {
                    GET { _, _ ->
                        capturedMethod = request.method
                        capturedTarget = request.target
                        ok("ok")
                    }
                }
            }
        val r = router { route(root) }

        val req = RequestDTO(Method.GET, "/api/123", mutableMapOf("Custom" to "header"), "body")
        r.dispatch(req)

        assertEquals(Method.GET, capturedMethod)
        assertEquals("/api/123", capturedTarget)
    }

    @Test
    fun `test group with POST method and body`() {
        val root =
            groupRoute("/api") {
                group("/items") {
                    POST { _, _ ->
                        ok("Created: ${request.body}")
                    }
                }
            }

        val req = RequestDTO(Method.POST, "/api/items", mutableMapOf(), "item data")
        val r = router { addRoute(root) }
        val resp = r.dispatch(req)
        assertEquals("Created: item data", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group after middleware is invoked`() {
        var afterCalled = false

        val root =
            groupRoute("/api") {
                group("/test") {
                    after(
                        relayAfter {
                            afterCalled = true
                        },
                    )

                    GET { _, _ -> ok("response") }
                }
            }

        val r = router { addRoute(root) }

        val req = RequestDTO(Method.GET, "/api/test", mutableMapOf(), "")
        val resp = r.dispatch(req)
        root.flatten().last().middlewareProcessAfter(Result.success(resp))

        assert(afterCalled) { "After middleware should be called" }
    }

    @Test
    fun `test group with empty path segment`() {
        val root =
            groupRoute("") {
                group("/test") {
                    GET { _, _ -> ok("empty root") }
                }
            }
        assertThrows<RouteNoTargetException> { router { addRoute(root) } }
    }

    @Test
    fun `test group with numeric dynamic segment`() {
        val root =
            groupRoute("/items") {
                group("/{id}") {
                    GET { _, query ->
                        val id = query["id"]
                        ok("Item: $id")
                    }
                }
            }

        val req = RequestDTO(Method.GET, "/items/999", mutableMapOf(), "")
        val r = router { addRoute(root) }
        val resp = r.dispatch(req)
        assertEquals("Item: 999", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with multiple optional segments`() {
        val root =
            groupRoute("/search") {
                group("/{query}/{page?}") {
                    GET { _, q ->
                        val query = q["query"]
                        val page = q["page"]
                        ok("Query: $query, Page: ${if (page?.isNotEmpty() == true) page else "1"}")
                    }
                }
            }

        val r = router { route(root) }

        // With optional
        val req = RequestDTO(Method.GET, "/search/kotlin/2", mutableMapOf(), "")
        assertEquals("Query: kotlin, Page: 2", (r.dispatch(req).body as ResponseBody.StringBody).body)

        // Without optional
        val req2 = RequestDTO(Method.GET, "/search/kotlin", mutableMapOf(), "")
        assertEquals("Query: kotlin, Page: 1", (r.dispatch(req2).body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group route matching prioritizes longer paths`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { _, _ -> ok("users") }
                }
                group("/users/admin") {
                    GET { _, _ -> ok("admin users") }
                }
            }

        val r = router { addRoute(root) }
        val req = RequestDTO(Method.GET, "/api/users/admin", mutableMapOf(), "")
        val resp = r.dispatch(req)
        assertEquals("admin users", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test nested groups inherit parent middleware before and after`() {
        var parentBeforeCalled = false
        var childBeforeCalled = false
        var parentAfterCalled = false
        var childAfterCalled = false

        val root =
            groupRoute("/api") {
                before(
                    relayBefore {
                        parentBeforeCalled = true
                        null
                    },
                )
                after(relayAfter { parentAfterCalled = true })

                group("/v1") {
                    before(
                        relayBefore {
                            childBeforeCalled = true
                            null
                        },
                    )
                    after(relayAfter { childAfterCalled = true })

                    GET { _, _ -> ok("test") }
                }
            }

        val req = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.flatten().last().middlewareProcessBefore(req)
        val resp = root.content(req, emptyMap())
        root.flatten().last().middlewareProcessAfter(Result.success(resp))

        assert(parentBeforeCalled) { "Parent before middleware should be called" }
        assert(childBeforeCalled) { "Child before middleware should be called" }
        assert(parentAfterCalled) { "Parent after middleware should be called" }
        assert(childAfterCalled) { "Child after middleware should be called" }
    }

    @Test
    fun `test group with all dynamic segments`() {
        val root =
            groupRoute("/{a}") {
                group("/{b}") {
                    group("/{c}") {
                        GET { _, query ->
                            val a = query["a"]
                            val b = query["b"]
                            val c = query["c"]
                            ok("$a/$b/$c")
                        }
                    }
                }
            }

        val req = RequestDTO(Method.GET, "/x/y/z", mutableMapOf(), "")
        val r = router { addRoute(root) }
        val resp = r.dispatch(req)
        assertEquals("x/y/z", (resp.body as ResponseBody.StringBody).body)
    }
}
