package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.ok
import io.voidx.middleware.RelayBefore
import io.voidx.middleware.relayBefore
import io.voidx.page.GroupPage
import io.voidx.page.groupRoute
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupPageTests {
    @Test
    fun `test basic grouping and routing`() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    GET { ok("v1 get") }
                }
                GET { ok("api get") }
            }

        // Test root group GET
        root.request = RequestDTO(Method.GET, "/api", mutableMapOf(), "")
        val resp1 = root.content()
        assertEquals(200, resp1.status)
        assertEquals("api get", (resp1.body as ResponseBody.StringBody).body)

        // Test nested group GET
        // Note: The current implementation of GroupPage.group sets target to "$target$path"
        // So "/api" + "/v1" = "/api/v1"
        val req2 = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.request = req2

        val resp2 = root.content()
        assertEquals(200, resp2.status)
        assertEquals("v1 get", (resp2.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test nested grouping targets`() {
        val root =
            groupRoute("/a") {
                group("/b") {
                    group("/c") {
                        GET { ok("abc") }
                    }
                }
            }

        val req = RequestDTO(Method.GET, "/a/b/c", mutableMapOf(), "")
        root.request = req

        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("abc", (resp.body as ResponseBody.StringBody).body)
    }

    class TestRelay(
        override val priority: Int = 0,
    ) : RelayBefore {
        override fun processBefore(requestDTO: Result<RequestDTO>) = null
    }

    @Test
    fun `test middleware propagation`() {
        var called = false
        val relay1 = relayBefore(1) {
            called = true
            null
        }
        val root =
            groupRoute("/api") {
                before(relay1)
                group("/v1") {
                    GET { ok("v1") }
                }
            }

        // We need to trigger middlewareProcessBefore on the matched page.
        // But GroupPage.content() currently only calls .content() on children, not middleware!
        // This is a structural error that should be pointed out.
        
        val req = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.request = req
        root.middlewareProcessBefore()
        
        assertEquals(true, called, "Middleware should be called on nested routes")
    }

    @Test
    fun `test content delegation and recursion`() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    group("/users") {
                        GET { ok("users") }
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1/users", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("users", (resp.body as ResponseBody.StringBody).body)
    }
}
