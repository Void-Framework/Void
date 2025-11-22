package test

import io.voidx.dto.ok
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.router.Router
import io.voidx.router.exceptions.RouteNoTargetException
import io.voidx.router.exceptions.RouteTargetUsedException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class RouterRouteValidationTests {
    @Test
    fun addRoute_throws_when_target_missing_leading_slash() {
        val r = Router()
        // Intentionally omit leading slash
        val bad: Page = route("no-slash") { GET { ok("x", mutableMapOf("Content-Type" to "text/plain")) } }
        assertFailsWith<RouteNoTargetException> { r.addRoute(bad) }
    }

    @Test
    fun addRoute_throws_when_duplicate_target_used() {
        val r = Router()
        val p1: Page = route("/dup") { GET { ok("1", mutableMapOf("Content-Type" to "text/plain")) } }
        val p2: Page = route("/dup") { GET { ok("2", mutableMapOf("Content-Type" to "text/plain")) } }
        r.addRoute(p1)
        assertFailsWith<RouteTargetUsedException> { r.addRoute(p2) }
    }
}
