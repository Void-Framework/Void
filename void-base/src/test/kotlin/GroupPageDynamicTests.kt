package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.ok
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupPageDynamicTests {
    @Test
    fun `test dynamic segments in group paths`() {
        val root = groupRoute("/users") {
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
        val root = groupRoute("/org") {
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
}
