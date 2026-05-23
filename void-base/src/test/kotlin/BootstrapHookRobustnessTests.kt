package test

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.ok
import io.voidx.page.route
import io.voidx.router.router
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertEquals

private class SockBR(
    private val raw: String,
) : Socket() {
    private val inBytes = ByteArrayInputStream(raw.toByteArray())
    private val outBytes = ByteArrayOutputStream()

    override fun getInputStream(): InputStream = inBytes

    override fun getOutputStream(): OutputStream = outBytes

    fun text(): String = outBytes.toString().replace("\r\n", "\n")
}

class BootstrapHookRobustnessTests {
    @Test
    fun page_decorators_are_isolated_when_one_throws() {
        val called = mutableListOf<String>()
        val r = router { }
        val h1 = Bootstrap.addPageDecorator { _, _ -> called += "ok1" }
        val h2 = Bootstrap.addPageDecorator { _, _ -> throw RuntimeException("boom") }
        val h3 = Bootstrap.addPageDecorator { _, _ -> called += "ok3" }
        try {
            r.addRoute(route("/x") { GET { _, _ -> ok("x") } })
            assertEquals(listOf("ok1", "ok3"), called)
        } finally {
            h1.close()
            h2.close()
            h3.close()
        }
    }

    @Test
    fun error_handlers_are_isolated_when_one_throws() {
        val called = mutableListOf<String>()
        val h1 = Bootstrap.addErrorHandler { _, _ -> called += "ok1" }
        val h2 = Bootstrap.addErrorHandler { _, _ -> throw RuntimeException("boom") }
        val h3 = Bootstrap.addErrorHandler { _, _ -> called += "ok3" }
        try {
            val r = router { }
            val sock = SockBR("")
            r.error(sock, RuntimeException("err"), 1.1)
            assertEquals(listOf("ok1", "ok3"), called)
        } finally {
            h1.close()
            h2.close()
            h3.close()
        }
    }
}
