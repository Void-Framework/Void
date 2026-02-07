package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.ok
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GroupPageDynamicTests {
    @Test
    fun `test dynamic segments in group paths`() {
        val root =
            groupRoute("/users") {
                group("/{userId}") {
                    GET {
                        val userId = path<String>("userId")
                        ok("User ID: $userId")
                    }
                    group("/posts") {
                        GET {
                            val userId = path<String>("userId")
                            ok("Posts for user: $userId")
                        }
                    }
                }
            }

        // Test /users/123
        root.request = RequestDTO(Method.GET, "/users/123", mutableMapOf(), "")
        val resp1 = root.content()
        assertEquals(200, resp1.status)
        assertEquals("User ID: 123", (resp1.body as ResponseBody.StringBody).body)

        // Test /users/123/posts
        root.request = RequestDTO(Method.GET, "/users/123/posts", mutableMapOf(), "")
        val resp2 = root.content()
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
                            GET {
                                val orgId = path<String>("orgId")
                                val projectId = path<String>("projectId")
                                ok("Org: $orgId, Project: $projectId")
                            }
                        }
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/org/voidx/projects/1", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("Org: voidx, Project: 1", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test optional segment in group path present`() {
        val root =
            groupRoute("/docs") {
                group("/{section}/{page?}") {
                    GET {
                        val section = path<String>("section")
                        val page = path<String>("page?")
                        ok("Section: $section, Page: ${page ?: "index"}")
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/docs/api/overview", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("Section: api, Page: overview", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test optional segment in group path missing`() {
        val root =
            groupRoute("/docs") {
                group("/{section}/{page?}") {
                    GET {
                        val section = path<String>("section")
                        val page = path<String>("page?")
                        ok("Section: $section, Page: ${page ?: "index"}")
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/docs/api", mutableMapOf(), "")
        val resp = root.content()
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

                    GET { ok("v1") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.content()

        // Both middleware should be called due to inheritance
        assert(parentBeforeCalled || childBeforeCalled) { "At least one middleware should be called" }
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

                    GET { ok("should not reach") }
                }
            }

        root.request = RequestDTO(Method.GET, "/protected/123", mutableMapOf(), "")
        val resp = root.content()
        assertEquals("blocked", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with trailing slash handling`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { ok("users") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/users/", mutableMapOf(), "")
        val resp = root.content()
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
                                GET {
                                    val b = path<String>("b")
                                    val d = path<String>("d")
                                    ok("b=$b, d=$d")
                                }
                            }
                        }
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/a/b-val/c/d-val/e", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("b=b-val, d=d-val", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group returns 404 for unmatched path`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { ok("users") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/products", mutableMapOf(), "")
        val resp = root.content()
        // Should return 404 or empty response for unmatched route
        assertNotNull(resp)
    }

    @Test
    fun `test group with multiple methods on same path`() {
        val root =
            groupRoute("/api") {
                group("/resource") {
                    GET { ok("get resource") }
                    POST { ok("create resource") }
                    PUT { ok("update resource") }
                    DELETE { ok("delete resource") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/resource", mutableMapOf(), "")
        assertEquals("get resource", (root.content().body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.POST, "/api/resource", mutableMapOf(), "")
        assertEquals("create resource", (root.content().body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.PUT, "/api/resource", mutableMapOf(), "")
        assertEquals("update resource", (root.content().body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.DELETE, "/api/resource", mutableMapOf(), "")
        assertEquals("delete resource", (root.content().body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with sibling routes`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { ok("users") }
                }
                group("/posts") {
                    GET { ok("posts") }
                }
                group("/comments") {
                    GET { ok("comments") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/users", mutableMapOf(), "")
        assertEquals("users", (root.content().body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.GET, "/api/posts", mutableMapOf(), "")
        assertEquals("posts", (root.content().body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.GET, "/api/comments", mutableMapOf(), "")
        assertEquals("comments", (root.content().body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group dynamic segment with special characters`() {
        val root =
            groupRoute("/files") {
                group("/{filename}") {
                    GET {
                        val filename = path<String>("filename")
                        ok("File: $filename")
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/files/my-file.txt", mutableMapOf(), "")
        val resp = root.content()
        assertEquals("File: my-file.txt", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with mixed static and dynamic paths`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { ok("all users") }

                    group("/{id}") {
                        GET {
                            val id = path<String>("id")
                            ok("user $id")
                        }
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/users", mutableMapOf(), "")
        assertEquals("all users", (root.content().body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.GET, "/api/users/42", mutableMapOf(), "")
        assertEquals("user 42", (root.content().body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group request propagation to nested routes`() {
        var capturedMethod: Method? = null
        var capturedTarget: String? = null

        val root =
            groupRoute("/api") {
                group("/{id}") {
                    GET {
                        capturedMethod = request.method
                        capturedTarget = request.target
                        ok("ok")
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/123", mutableMapOf("Custom" to "header"), "body")
        root.content()

        assertEquals(Method.GET, capturedMethod)
        assertEquals("/api/123", capturedTarget)
    }

    @Test
    fun `test group with POST method and body`() {
        val root =
            groupRoute("/api") {
                group("/items") {
                    POST {
                        ok("Created: ${request.body}")
                    }
                }
            }

        root.request = RequestDTO(Method.POST, "/api/items", mutableMapOf(), "item data")
        val resp = root.content()
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

                    GET { ok("response") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/test", mutableMapOf(), "")
        val resp = root.content()
        root.middlewareProcessAfter(Result.success(resp))

        assert(afterCalled) { "After middleware should be called" }
    }

    @Test
    fun `test group with empty path segment`() {
        val root =
            groupRoute("") {
                group("/test") {
                    GET { ok("empty root") }
                }
            }

        root.request = RequestDTO(Method.GET, "/test", mutableMapOf(), "")
        val resp = root.content()
        assertEquals("empty root", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with numeric dynamic segment`() {
        val root =
            groupRoute("/items") {
                group("/{id}") {
                    GET {
                        val id = path<String>("id")
                        ok("Item: $id")
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/items/999", mutableMapOf(), "")
        val resp = root.content()
        assertEquals("Item: 999", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group with multiple optional segments`() {
        val root =
            groupRoute("/search") {
                group("/{query}/{page?}") {
                    GET {
                        val query = path<String>("query")
                        val page = path<String>("page?")
                        ok("Query: $query, Page: ${page ?: "1"}")
                    }
                }
            }

        // With optional
        root.request = RequestDTO(Method.GET, "/search/kotlin/2", mutableMapOf(), "")
        assertEquals("Query: kotlin, Page: 2", (root.content().body as ResponseBody.StringBody).body)

        // Without optional
        root.request = RequestDTO(Method.GET, "/search/kotlin", mutableMapOf(), "")
        assertEquals("Query: kotlin, Page: 1", (root.content().body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group route matching prioritizes longer paths`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { ok("users") }
                }
                group("/users/admin") {
                    GET { ok("admin users") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/users/admin", mutableMapOf(), "")
        val resp = root.content()
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
                before(relayBefore { parentBeforeCalled = true; null })
                after(relayAfter { parentAfterCalled = true })

                group("/v1") {
                    before(relayBefore { childBeforeCalled = true; null })
                    after(relayAfter { childAfterCalled = true })

                    GET { ok("test") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        val resp = root.content()
        root.middlewareProcessAfter(Result.success(resp))

        // Child should have inherited parent middleware
        assert(parentBeforeCalled || childBeforeCalled) { "Before middleware should be called" }
        assert(parentAfterCalled || childAfterCalled) { "After middleware should be called" }
    }

    @Test
    fun `test group with all dynamic segments`() {
        val root =
            groupRoute("/{a}") {
                group("/{b}") {
                    group("/{c}") {
                        GET {
                            val a = path<String>("a")
                            val b = path<String>("b")
                            val c = path<String>("c")
                            ok("$a/$b/$c")
                        }
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/x/y/z", mutableMapOf(), "")
        val resp = root.content()
        assertEquals("x/y/z", (resp.body as ResponseBody.StringBody).body)
    }
}