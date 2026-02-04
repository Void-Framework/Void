package io.voidx.page

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.ok
import io.voidx.middleware.RelayBefore
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.page.GroupPage
import io.voidx.page.groupRoute
import io.voidx.util.toResult
import org.junit.jupiter.api.Assertions.assertNotNull
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
        val relay1 =
            relayBefore(1) {
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

    @Test
    fun `test exact path matching prevents partial matches`() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    GET { ok("v1") }
                }
                group("/v1-beta") {
                    GET { ok("v1-beta") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1-beta", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("v1-beta", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test path boundary detection`() {
        val root =
            groupRoute("/api") {
                group("/users") {
                    GET { ok("users") }
                }
            }

        // Should NOT match /api/users-admin
        root.request = RequestDTO(Method.GET, "/api/users-admin", mutableMapOf(), "")
        val resp = root.content()
        // Since no match, should return a 404
        assertEquals(404, resp.status)
    }

    @Test
    fun `test nested groups with different HTTP methods`() {
        val root =
            groupRoute("/api") {
                group("/resources") {
                    GET { ok("get resources") }
                    POST { ok("post resources") }
                    PUT { ok("put resources") }
                    DELETE { ok("delete resources") }
                }
            }

        root.request = RequestDTO(Method.POST, "/api/resources", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("post resources", (resp.body as ResponseBody.StringBody).body)

        root.request = RequestDTO(Method.DELETE, "/api/resources", mutableMapOf(), "")
        val resp2 = root.content()
        assertEquals(200, resp2.status)
        assertEquals("delete resources", (resp2.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test group inherits parent relays at creation time`() {
        val relay1 =
            relayBefore(1) {
                null
            }
        val relay2 =
            relayBefore(2) {
                null
            }

        val root =
            groupRoute("/api") {
                before(relay1)
                group("/v1") {
                    GET { ok("v1") }
                }
                before(relay2) // added after group creation
            }

        // The nested group should only have relay1
        val nestedGroup = root.routes.first() as GroupPage
        assertEquals(1, nestedGroup.relaysBefore.size)
    }

    @Test
    fun `test request propagates to child routes`() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    GET { ok(request.target) }
                }
            }

        val req = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.request = req

        // Child routes should also have the request
        val child = root.routes.first()
        assertEquals(req, child.request)
    }

    @Test
    fun `test after middleware on nested route`() {
        var afterCalled = false
        val relay =
            relayAfter(1) {
                afterCalled = true
            }

        val root =
            groupRoute("/api") {
                after(relay)
                group("/v1") {
                    GET { ok("v1") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.middlewareProcessBefore()
        val resp = root.content()
        root.middlewareProcessAfter(resp.toResult())

        assertEquals(true, afterCalled)
    }

    @Test
    fun `test deeply nested groups resolve correctly`() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    group("/resources") {
                        group("/users") {
                            group("/profile") {
                                GET { ok("deep") }
                            }
                        }
                    }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1/resources/users/profile", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("deep", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test empty response when no handler matches method`() {
        val root =
            groupRoute("/api") {
                group("/v1") {
                    GET { ok("get only") }
                }
            }

        root.request = RequestDTO(Method.POST, "/api/v1", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(405, resp.status) // empty response
    }

    @Test
    fun `test middleware on specific nested route executes`() {
        var middlewareCalled = false
        val root =
            groupRoute("/api") {
                group("/v1") {
                    before(
                        relayBefore {
                            middlewareCalled = true
                            null
                        },
                    )
                    GET { ok("v1") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        root.middlewareProcessBefore()
        assertEquals(true, middlewareCalled)
    }

    @Test
    fun `test root group can handle requests directly`() {
        val root =
            groupRoute("/api") {
                GET { ok("api root") }
                group("/v1") {
                    GET { ok("v1") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("api root", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test middleware before short circuits with response`() {
        val root =
            groupRoute("/api") {
                before(
                    relayBefore {
                        ok("blocked")
                    },
                )
                group("/v1") {
                    GET { ok("should not reach") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        val middlewareResp = root.middlewareProcessBefore()
        assertNotNull(middlewareResp)
        assertEquals("blocked", (middlewareResp?.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test multiple groups at same level`() {
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

        root.request = RequestDTO(Method.GET, "/api/posts", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("posts", (resp.body as ResponseBody.StringBody).body)
    }

    @Test
    fun `test longer paths are matched first`() {
        val root =
            groupRoute("/api") {
                group("/v") {
                    GET { ok("short") }
                }
                group("/v1") {
                    GET { ok("longer") }
                }
            }

        root.request = RequestDTO(Method.GET, "/api/v1", mutableMapOf(), "")
        val resp = root.content()
        assertEquals(200, resp.status)
        assertEquals("longer", (resp.body as ResponseBody.StringBody).body)
    }
}