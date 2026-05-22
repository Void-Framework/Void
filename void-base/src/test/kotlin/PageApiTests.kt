package test

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseBody
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.middleware.RelayAfter
import io.voidx.middleware.RelayBefore
import io.voidx.page.Page
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestBefore : RelayBefore {
    override val priority: Int = 0

    override fun processBefore(requestDTO: Result<RequestDTO>): ResponseDTO? =
        buildResponse<String> {
            status = 401
            statusText = "Unauthorized"
            body = "nope"
        }
}

private class TestAfter : RelayAfter {
    override val priority: Int = 0
    var observedStatus: Int? = null

    override fun processAfter(response: Result<ResponseDTO>) {
        observedStatus = response.getOrNull()?.status
    }
}

private class P : Page("/p") {
    override fun content(request: RequestDTO, queries: Map<String, String>): ResponseDTO =
        buildResponse<String> {
            status = 200
            statusText = "OK"
            body = "page"
        }
}

class PageApiTests {
    @Test
    fun page_defaults_and_middleware_via_kclass_and_instance() {
        val page = P()

        // Register BEFORE via KClass (createInstance path) with unchecked cast to match signature
        @Suppress("UNCHECKED_CAST")
        val k: kotlin.reflect.KClass<io.voidx.middleware.RelayBefore> = TestBefore::class as kotlin.reflect.KClass<io.voidx.middleware.RelayBefore>
        page.before(k)

        // Register AFTER via instance
        val after = TestAfter()
        page.after(after)

        // Simulate request bound to page
        val req = RequestDTO(method = io.voidx.Method.GET, target = "/p", headers = mutableMapOf(), body = "")

        // BEFORE should short-circuit and return a response
        val short = page.middlewareProcessBefore(req)
        assertNotNull(short)
        assertEquals(401, short.status)
        assertTrue(short.body is ResponseBody.StringBody)

        // AFTER should observe that response
        page.middlewareProcessAfter(Result.success(short))
        assertEquals(401, after.observedStatus)
    }
}
