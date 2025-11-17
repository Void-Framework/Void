package test

import io.voidx.Method
import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.dto.buildResponse
import io.voidx.middleware.relayAfter
import io.voidx.middleware.relayBefore
import io.voidx.router.Router
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MiddlewareInternalTests {
    @Test
    fun router_global_before_short_circuits_and_after_sees_result() {
        val router = Router()

        // Register BEFORE that always short-circuits
        with(router) {
            +relayBefore { _ ->
                buildResponse<String> {
                    status = 403
                    statusText = "Forbidden"
                    body = "nope"
                }
            }
        }

        // Register AFTER that records the status it observed
        var observedStatus: Int? = null
        with(router) {
            +relayAfter { r -> observedStatus = r.getOrNull()?.status }
        }

        // Manually invoke internal processing functions
        val req =
            buildRequest {
                method = Method.GET
                target = "/x"
            }
        val before = router.middlewareProcessBefore(Result.success(req))
        assertNotNull(before)
        assertEquals(403, before.status)

        router.middlewareProcessAfter(Result.success(before))
        assertEquals(403, observedStatus)

        // Sanity: body content propagated
        val body = before.body
        assertTrue(body is ResponseBody.StringBody && body.body == "nope")
    }

    @Test
    fun global_middleware_priority_ordering_descending() {
        val router = Router()
        val order = mutableListOf<Int>()

        with(router) {
            +relayBefore(priority = 1) { _ ->
                order += 1
                null
            }
            +relayBefore(priority = 10) { _ ->
                order += 10
                null
            }
            +relayBefore(priority = 5) { _ ->
                order += 5
                null
            }
        }

        val req =
            buildRequest {
                method = Method.GET
                target = "/"
            }
        val result = router.middlewareProcessBefore(Result.success(req))
        // No short-circuit
        assertEquals(null, result)
        // Should execute in priority DESC: 10, 5, 1
        assertEquals(listOf(10, 5, 1), order)
    }
}
