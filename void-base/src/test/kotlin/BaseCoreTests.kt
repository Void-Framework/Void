package test

import io.voidx.Method
import io.voidx.dto.*
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.page.path
import io.voidx.page.route
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BaseCoreTests {
    @Test
    fun request_builder_and_headers_roundtrip() {
        val req =
            buildRequest {
                method = Method.POST
                target = "/api/items?x=1"
                headers { put("Content-Type", "text/plain") }
                body = "hello"
            }

        assertEquals(Method.POST, req.method)
        assertEquals("/api/items?x=1", req.target)
        assertEquals("text/plain", req["Content-Type"])

        val httpReq = req.toHttpRequest("http://localhost:8080")
        assertEquals("http://localhost:8080/api/items?x=1", httpReq.uri().toString())
        assertEquals("POST", httpReq.method())
    }

    @Test
    fun response_helpers_build_and_empty() {
        val r1 =
            buildResponse<String> {
                status = 200
                statusText = "OK"
                headers["Content-Type"] = "text/plain"
                body = "hi"
            }
        assertEquals(200, r1.status)
        assertEquals("OK", r1.statusText)
        assertEquals("text/plain", r1.headers["Content-Type"])
        val body1 = r1.body
        assertTrue(body1 is ResponseBody.StringBody && body1.body == "hi")

        val r2 = emptyResponse()
        // default values from builder
        assertEquals(200, r2.status)
        assertEquals("OK", r2.statusText)
    }

    @Test
    fun page_middleware_before_after_and_short_circuit() {
        var afterCalled = false
        val page =
            route("/hello") {
                GET {
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        body = "handled"
                    }
                }
            }

        // BEFORE short-circuits to custom response
        page.before(
            relayBefore(priority = 10) { _ ->
                buildResponse<String> {
                    status = 401
                    statusText = "Unauthorized"
                    body = "nope"
                }
            },
        )

        // AFTER should observe the short-circuited response
        page.after(
            relayAfter { resp ->
                val r = resp.getOrNull()
                afterCalled = (r?.status == 401)
            },
        )

        val req = buildRequest { target = "/hello" }
        page.request = req
        val short = page.middlewareProcessBefore()
        assertNotNull(short)
        page.middlewareProcessAfter(Result.success(short))
        assertTrue(afterCalled)
    }

    @Test
    fun dynamic_page_path_accessor_and_execution() {
        val dyn =
            route("/users/{id}/{name?}") {
                GET {
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        val id: String? = path("id")
                        val name: String? = path("name?")
                        body = "$id:${name ?: "-"}"
                    }
                }
            }

        // Simulate router-populated dynamic values
        dyn._data["id"] = "42"
        dyn._data["name?"] = "neo"

        val req =
            buildRequest {
                method = Method.GET
                target = "/users/42/neo"
            }
        dyn.request = req
        val resp = dyn.content()
        val body = (resp.body as ResponseBody.StringBody).body
        assertEquals("42:neo", body)
    }
}
