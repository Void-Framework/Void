package test

import io.voidx.Method
import io.voidx.Server
import io.voidx.dto.buildRequest
import io.voidx.dto.ok
import io.voidx.fetch
import io.voidx.page.route
import io.voidx.router.router
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue

class BenchmarkTests {
    companion object {
        private lateinit var server: Server
        private const val PORT = 9999
        private const val BASE_URL = "http://localhost:$PORT"

        @JvmStatic
        @BeforeAll
        fun setup() {
            val latch = CountDownLatch(1)
            val r =
                router {
                    route("/ping") {
                        GET { _, _ -> ok("pong") }
                    }
                    route("/echo") {
                        POST { req, _ -> ok(req.body) }
                    }
                    route("/user/{id}") {
                        GET { _, _ -> ok("user ${data["id"]}") }
                    }
                }
            server = Server(r)
            Thread {
                try {
                    server.startHTTPServer(PORT)
                    latch.countDown()
                } catch (e: Exception) {
                    System.err.println("Server failed to start: ${e.message}")
                    throw e
                }
            }.start()

            // Give server time to start
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw IllegalStateException("Server failed to start within timeout")
            }
        }

        @JvmStatic
        @AfterAll
        fun teardown() {
            if (::server.isInitialized) {
                server.stop()
            }
        }
    }

    @Test
    fun `benchmark ping throughput`() {
        val iterations = 2000
        val warmUp = 500

        // Warm up
        repeat(warmUp) {
            fetch(BASE_URL, buildRequest { target = "/ping" })
        }

        val time =
            measureTimeMillis {
                repeat(iterations) {
                    val resp = fetch(BASE_URL, buildRequest { target = "/ping" })
                    assertTrue(resp.isSuccess)
                }
            }

        val avgLatency = time.toDouble() / iterations
        // Using System.out.println to ensure it shows up in some environments if [DEBUG_LOG] doesn't
        System.out.println("[DEBUG_LOG] Average latency: $avgLatency ms")
        System.out.println("[DEBUG_LOG] Throughput: ${iterations.toDouble() / (time.toDouble() / 1000)} req/s")

        // Threshold: 10ms per request on average seems reasonable for a local simple framework.
        assertTrue(avgLatency < 1.0, "Average latency $avgLatency ms exceeds threshold of 1 ms")
    }

    @Test
    fun `benchmark dynamic routing`() {
        val iterations = 2000
        val warmUp = 500

        repeat(warmUp) {
            fetch(BASE_URL, buildRequest { target = "/user/123" })
        }

        val time =
            measureTimeMillis {
                repeat(iterations) {
                    val resp = fetch(BASE_URL, buildRequest { target = "/user/$it" })
                    assertTrue(resp.isSuccess)
                }
            }

        val avgLatency = time.toDouble() / iterations
        System.out.println("[DEBUG_LOG] Dynamic route average latency: $avgLatency ms")

        assertTrue(avgLatency < 2.0, "Dynamic route average latency $avgLatency ms exceeds threshold of 2 ms")
    }
}
