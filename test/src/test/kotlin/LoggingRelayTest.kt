package test

import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.apiRoute
import io.void.middleware.logAfter
import io.void.middleware.logBefore
import io.void.middleware.traceId
import io.void.router.router
import io.void.server.server
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.Marker
import java.net.ServerSocket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class LoggingRelayTest {

    private fun freePort(): Int = ServerSocket(0).use { it.localPort }

    private class CapturingLogger : Logger {
        var lastMessage: String? = null
        var lastArgs: Array<out Any?>? = null

        override fun getName(): String = "capturing"
        override fun isTraceEnabled(): Boolean = false
        override fun trace(msg: String?) {}
        override fun trace(format: String?, arg: Any?) {}
        override fun trace(format: String?, arg1: Any?, arg2: Any?) {}
        override fun trace(format: String?, vararg arguments: Any?) {}
        override fun trace(msg: String?, t: Throwable?) {}
        override fun isTraceEnabled(marker: Marker?): Boolean = false
        override fun trace(marker: Marker?, msg: String?) {}
        override fun trace(marker: Marker?, format: String?, arg: Any?) {}
        override fun trace(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {}
        override fun trace(marker: Marker?, format: String?, vararg arguments: Any?) {}
        override fun trace(marker: Marker?, msg: String?, t: Throwable?) {}

        override fun isDebugEnabled(): Boolean = false
        override fun debug(msg: String?) {}
        override fun debug(format: String?, arg: Any?) {}
        override fun debug(format: String?, arg1: Any?, arg2: Any?) {}
        override fun debug(format: String?, vararg arguments: Any?) {}
        override fun debug(msg: String?, t: Throwable?) {}
        override fun isDebugEnabled(marker: Marker?): Boolean = false
        override fun debug(marker: Marker?, msg: String?) {}
        override fun debug(marker: Marker?, format: String?, arg: Any?) {}
        override fun debug(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {}
        override fun debug(marker: Marker?, format: String?, vararg arguments: Any?) {}
        override fun debug(marker: Marker?, msg: String?, t: Throwable?) {}

        override fun isInfoEnabled(): Boolean = true
        override fun info(msg: String?) {}
        override fun info(format: String?, arg: Any?) { lastMessage = format; lastArgs = arrayOf(arg) }
        override fun info(format: String?, arg1: Any?, arg2: Any?) { lastMessage = format; lastArgs = arrayOf(arg1, arg2) }
        override fun info(format: String?, vararg arguments: Any?) { lastMessage = format; lastArgs = arguments }
        override fun info(msg: String?, t: Throwable?) {}
        override fun isInfoEnabled(marker: Marker?): Boolean = true
        override fun info(marker: Marker?, msg: String?) {}
        override fun info(marker: Marker?, format: String?, arg: Any?) { info(format, arg) }
        override fun info(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) { info(format, arg1, arg2) }
        override fun info(marker: Marker?, format: String?, vararg arguments: Any?) { info(format, *arguments) }
        override fun info(marker: Marker?, msg: String?, t: Throwable?) {}

        override fun isWarnEnabled(): Boolean = false
        override fun warn(msg: String?) {}
        override fun warn(format: String?, arg: Any?) {}
        override fun warn(format: String?, arg1: Any?, arg2: Any?) {}
        override fun warn(format: String?, vararg arguments: Any?) {}
        override fun warn(msg: String?, t: Throwable?) {}
        override fun isWarnEnabled(marker: Marker?): Boolean = false
        override fun warn(marker: Marker?, msg: String?) {}
        override fun warn(marker: Marker?, format: String?, arg: Any?) {}
        override fun warn(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {}
        override fun warn(marker: Marker?, format: String?, vararg arguments: Any?) {}
        override fun warn(marker: Marker?, msg: String?, t: Throwable?) {}

        override fun isErrorEnabled(): Boolean = false
        override fun error(msg: String?) {}
        override fun error(format: String?, arg: Any?) {}
        override fun error(format: String?, arg1: Any?, arg2: Any?) {}
        override fun error(format: String?, vararg arguments: Any?) {}
        override fun error(msg: String?, t: Throwable?) {}
        override fun isErrorEnabled(marker: Marker?): Boolean = false
        override fun error(marker: Marker?, msg: String?) {}
        override fun error(marker: Marker?, format: String?, arg: Any?) {}
        override fun error(marker: Marker?, format: String?, arg1: Any?, arg2: Any?) {}
        override fun error(marker: Marker?, format: String?, vararg arguments: Any?) {}
        override fun error(marker: Marker?, msg: String?, t: Throwable?) {}
    }

    @Test
    fun `logBefore preserves incoming X-Trace-Id and echoes via response header`() {
        val port = freePort()
        val logger = CapturingLogger()
        server {
            this.port = port
            router = router {
                +logBefore
                +logAfter(logger)
                val route = apiRoute("/logging") { request ->
                    buildResponse<String> {
                        status = 201
                        statusText = "Created"
                        body = ""
                        headers {
                            put("X-Trace-Id", request.traceId)
                        }
                    }
                }
                +route
            }
        }

        // make a client request
        val client = HttpClient.newHttpClient()
        val req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/logging"))
            .header("X-Trace-Id", "abc-123")
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString())

        assertEquals(201, res.statusCode())

        // assert logging
        assertEquals("[{}] {} {} -> {}", logger.lastMessage)
        val args = requireNotNull(logger.lastArgs)
        assertEquals("abc-123", args[0])
        assertEquals("POST", args[1].toString())
        assertEquals("/logging", args[2])
        assertEquals(201, args[3])
    }

    @Test
    fun `logBefore generates a trace id when header missing and echoes it`() {
        val port = freePort()
        val logger = CapturingLogger()
        server {
            this.port = port
            router = router {
                +logBefore
                +logAfter(logger)
                val route = apiRoute("/logging") { request ->
                    buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        body = ""
                        headers {
                            put("X-Trace-Id", request.traceId)
                        }
                    }
                }
                +route
            }
        }

        val client = HttpClient.newHttpClient()
        val req = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port/logging"))
            .GET()
            .build()
        val res = client.send(req, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, res.statusCode())

        // assert logging
        assertEquals("[{}] {} {} -> {}", logger.lastMessage)
        val args = requireNotNull(logger.lastArgs)
        assertTrue((args[0] as? String)?.isNotBlank() == true)
        assertEquals("GET", args[1].toString())
        assertEquals("/logging", args[2])
        assertEquals(200, args[3])
    }
}
