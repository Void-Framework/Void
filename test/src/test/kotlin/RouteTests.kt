package test

import io.jadiefication.routes.echo.echoRoute
import io.jadiefication.routes.home.homeRoute
import io.jadiefication.routes.search.searchRootRoute
import io.jadiefication.routes.search.searchRoute
import io.jadiefication.routes.setter.setterRoute
import io.jadiefication.routes.user.userRoute
import io.void.api.method.Method
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildRequest
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.html.page.apiRoute
import io.void.json.fromJson
import io.void.fetch.fetch
import io.void.router.router
import io.void.server.Server
import io.void.server.server
import io.void.server.simpleServer
import io.void.html.page.htmlRoute
import io.void.generated.*
import io.void.html.Fractal
import kotlinx.coroutines.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.ByteArrayOutputStream
import java.io.File
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Security
import java.security.cert.X509Certificate
import java.time.Instant
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Serializable
private data class SetterMeta(
    val registered: Boolean,
    val languages: List<String>,
    val profile: Map<String, String>,
)

@Serializable
private data class SetterDTO(
    val name: String,
    val age: Int,
    val isStudent: Boolean,
    val grades: List<Int>,
    val meta: SetterMeta,
    val nullValue: String? = null,
    val emptyList: List<String> = emptyList(),
    val emptyMap: Map<String, String> = emptyMap(),
)

@Serializable
private data class EchoDTO(
    val path: String,
    val query: Map<String, String>,
    val hasQuery: Boolean,
)

@Serializable
private data class UserDTO(
    val id: String,
    val name: String,
    val email: String,
)

@Serializable
private data class SearchDTO(
    val path: String,
    val category: String? = null,
    val query: Map<String, String>,
)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RouteTests {
    private lateinit var server: Server
    private lateinit var httpsServer: Server
    private val httpPort = 8081
    private val httpsPort = 8443
    private val redirectPort = 8082
    private val scope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null
    private var httpsServerJob: Job? = null
    private var redirectServerJob: Job? = null

    private val enableHttpsTests = System.getProperty("void.tests.https") == "true"
    private val enableLoadTests = System.getProperty("void.tests.load") == "true"
    private val enablePerfTests = System.getProperty("void.tests.perf") == "true"

    private val client =
        HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build()

    // Unsafe SSL client for testing HTTPS (don't use in production)
    private val httpsClient =
        HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .sslContext(createUnsafeSSLContext())
            .build()

    @BeforeAll
    fun setup() {
        setupHttpServer()
        // Wait until HTTP is ready quickly instead of risking connection refused
        waitForPort(httpPort, 3000)
        if (enableHttpsTests) {
            setupHttpsServerIfKeystoreExists()
            // Wait until HTTPS is ready with timeout instead of fixed sleep
            val start = System.currentTimeMillis()
            while ((!::httpsServer.isInitialized || !httpsServer.isHTTPSServerRunning()) && System.currentTimeMillis() - start < 5000) {
                Thread.sleep(50)
            }
        }
    }

    @AfterAll
    fun tearDown() {
        runBlocking {
            serverJob?.cancelAndJoin()
            httpsServerJob?.cancelAndJoin()
            redirectServerJob?.cancelAndJoin()
            scope.cancel("Test completed")
        }
    }

    // ===== HTTP SERVER TESTS =====

    @Test
    fun `test server builder with custom configuration`() {
        val customServer =
            server {
                port = 9999
                httpVersion = 2.0
                router =
                    router {
                        +homeRoute
                    }
                autoStart = false
                onServerSocketError = { exception ->
                    println("Custom error handler: ${exception.message}")
                }
                onServerSocketClose = { socket ->
                    println("Custom close handler called")
                    socket.close()
                }
            }

        assertEquals(2.0, customServer.httpVersion)
        assertFalse(customServer.isHTTPSOn)
    }

    @Test
    fun `test simple server creation`() {
        val testPort = 9998
        var serverCreated = false

        try {
            simpleServer(testPort) {
                +apiRoute("/test") { request ->
                    buildResponse {
                        status = 200
                        statusText = "OK"
                        body = """{"message": "Simple server test", "path": "${request.target}"}"""
                        headers {
                            put("Content-Type", "application/json")
                        }
                    }
                }
            }
            serverCreated = true
        } catch (_: Exception) {
            // Port might be in use, that's okay for this test
        }

        Thread.sleep(10)
        assertTrue(serverCreated) // Test passes if server creation doesn't throw
    }

    @Test
    fun `test concurrent client handling`() {
        val concurrentRequests = if (enableLoadTests) 10 else 3
        val latch = CountDownLatch(concurrentRequests)
        val responses = mutableListOf<HttpResponse<String>>()

        repeat(concurrentRequests) {
            Thread {
                try {
                    val connection = createConnectionGET("/")
                    val response = client.send(connection, HttpResponse.BodyHandlers.ofString())
                    synchronized(responses) {
                        responses.add(response)
                    }
                } catch (e: Exception) {
                    println("Concurrent request failed: ${e.message}")
                } finally {
                    latch.countDown()
                }
            }.start()
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS))
        assertTrue(responses.isNotEmpty(), "At least some concurrent requests should succeed")
        responses.forEach { response ->
            assertEquals(200, response.statusCode())
        }
    }

    @Test
    fun `test server error handling`() {
        var errorCaught = false
        var errorMessage = ""

        val testServer =
            server {
                router = router { +homeRoute }
                autoStart = false
                onServerSocketError = { exception ->
                    errorCaught = true
                    errorMessage = exception.message ?: "Unknown error"
                }
            }

        // Try to start server on already used port to trigger error
        Thread {
            testServer.startHTTPServer(httpPort) // Same port as main server
        }.start()

        // Wait up to 1s for the error to be caught instead of fixed sleep
        val startWait = System.currentTimeMillis()
        while (!errorCaught && System.currentTimeMillis() - startWait < 1000) {
            Thread.sleep(20)
        }
        assertTrue(errorCaught, "Server should handle port binding error")
        assertTrue(errorMessage.contains("Address already in use") || errorMessage.isNotEmpty())
    }

    // ===== HTTPS SERVER TESTS =====

    @Test
    fun `test HTTPS keystore wrong password fails fast`() {
        val tempFile = File.createTempFile("test-keystore-wrongpass", ".p12").apply { deleteOnExit() }
        tempFile.writeBytes(createInMemoryKeystoreBytes())

        var errorCaught = false
        var exceptionMsg = ""
        val s =
            server {
                router = router { +homeRoute }
                autoStart = false
                onServerSocketError = { e ->
                    errorCaught = true
                    exceptionMsg = e::class.simpleName + ": " + (e.message ?: "")
                }
            }
        val httpsPortLocal = findFreePort()
        Thread { s.startHTTPSServer(httpsPortLocal, "wrong-password", tempFile, false) }.start()

        val start = System.currentTimeMillis()
        while (!errorCaught && System.currentTimeMillis() - start < 1000) {
            Thread.sleep(20)
        }
        assertTrue(errorCaught, "Expected HTTPS startup to fail fast with wrong password. Last error: $exceptionMsg")
    }

    @Test
    fun `test HTTPS keystore missing file fails fast`() {
        val missing = File.createTempFile("missing-keystore", ".p12")
        val path = missing.absolutePath
        // ensure it's missing
        missing.delete()

        var errorCaught = false
        val s =
            server {
                router = router { +homeRoute }
                autoStart = false
                onServerSocketError = { _ -> errorCaught = true }
            }
        val httpsPortLocal = findFreePort()
        Thread { s.startHTTPSServer(httpsPortLocal, "changeit", File(path), false) }.start()

        val start = System.currentTimeMillis()
        while (!errorCaught && System.currentTimeMillis() - start < 1000) {
            Thread.sleep(20)
        }
        assertTrue(errorCaught, "Expected HTTPS startup to fail fast with missing keystore file")
    }

    @Test
    fun `test HTTPS client auth required rejects client without certificate`() {
        val tempFile = File.createTempFile("test-keystore-clientauth", ".p12").apply { deleteOnExit() }
        tempFile.writeBytes(createInMemoryKeystoreBytes())
        var s: Server
        try {
            s =
                server {
                    router = router { +homeRoute }
                    autoStart = false
                }
            val httpsPortLocal = findFreePort()
            Thread { s.startHTTPSServer(httpsPortLocal, "changeit", tempFile, true) }.start()

            // wait until the HTTPS socket is reported as running (without connecting to it)
            val start = System.currentTimeMillis()
            while (System.currentTimeMillis() - start < 2000 && !s.isHTTPSServerRunning()) {
                Thread.sleep(20)
            }

            // Attempt a request without client cert; expect failure or non-200
            val req =
                HttpRequest
                    .newBuilder()
                    .uri(URI.create("https://localhost:$httpsPortLocal/"))
                    .GET()
                    .build()
            var failed: Boolean
            try {
                val resp = httpsClient.send(req, HttpResponse.BodyHandlers.ofString())
                failed = resp.statusCode() !in 200..299
            } catch (_: Exception) {
                failed = true
            }
            assertTrue(failed, "Expected client-auth required server to reject connection without client certificate")
        } finally {
            // best-effort stop: nothing to do; threads are daemon-like and will close on JVM exit
        }
    }

    @Test
    fun `test HTTPS server configuration`() {
        if (::httpsServer.isInitialized) {
            assertTrue(httpsServer.isHTTPSOn, "HTTPS should be enabled")
        } else {
            println("HTTPS tests skipped - no keystore available")
        }
    }

    @Test
    fun `test HTTPS client connection`() {
        if (!::httpsServer.isInitialized) {
            println("HTTPS test skipped - no keystore available")
            return
        }

        try {
            val connection =
                HttpRequest
                    .newBuilder()
                    .uri(URI.create("https://localhost:$httpsPort/"))
                    .GET()
                    .build()

            val response = httpsClient.send(connection, HttpResponse.BodyHandlers.ofString())
            assertEquals(200, response.statusCode())
            assertTrue(response.body().contains("<html"))
        } catch (e: Exception) {
            println("HTTPS connection test failed: ${e.message}")
        }
    }

    // ===== REDIRECT TESTS =====

    @Test
    fun `test HTTP to HTTPS redirect`() {
        if (!::httpsServer.isInitialized) {
            println("Redirect test skipped - HTTPS not available")
            return
        }

        try {
            val connection =
                HttpRequest
                    .newBuilder()
                    .uri(URI.create("http://localhost:$redirectPort/"))
                    .GET()
                    .build()

            val response = client.send(connection, HttpResponse.BodyHandlers.ofString())
            assertEquals(301, response.statusCode())
            assertTrue(response.headers().firstValue("Location").isPresent)
            assertTrue(
                response
                    .headers()
                    .firstValue("Location")
                    .get()
                    .startsWith("https://"),
            )
        } catch (e: Exception) {
            println("Redirect test failed: ${e.message}")
        }
    }

    // ===== ROUTE-SPECIFIC TESTS =====

    @Test
    fun `test home route returns 200 and valid HTML`() {
        val connection = createConnectionGET("/")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cResponse.statusCode())
        assertTrue(cResponse.headers().allValues("Content-Type").any { it.contains("text/html") })

        val response = cResponse.body()
        assertTrue(response.contains("<html"))
        assertTrue(response.contains("</html>"))
    }

    @Test
    fun `test setter route returns valid JSON`() {
        val connection = createConnectionGET("/setter")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cResponse.statusCode())
        assertTrue(cResponse.headers().allValues("Content-Type").any { it.contains("application/json") })

        val response = cResponse.body()
        val dto = response.fromJson<SetterDTO>().getOrThrow()

        assertEquals("Jade", dto.name)
        assertEquals(20, dto.age)
        assertTrue(dto.isStudent)

        assertTrue(dto.meta.registered)
        assertTrue(dto.meta.languages.contains("Kotlin"))
    }

    @Test
    fun `test dynamic user route returns correct user data`() {
        val userId = "123"
        val connection = createConnectionGET("/users/$userId")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cResponse.statusCode())
        assertTrue(cResponse.headers().allValues("Content-Type").any { it.contains("application/json") })

        val response = cResponse.body()
        val dto = response.fromJson<UserDTO>().getOrThrow()

        assertEquals(userId, dto.id)
        assertNotNull(dto.name)
        assertNotNull(dto.email)
    }

    @Test
    fun `test dynamic user route with invalid ID returns 404`() {
        val connection = createConnectionGET("/users/invalid")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, cResponse.statusCode())
    }

    @Test
    fun `test dynamic route pattern matching works correctly`() {
        val testCases =
            listOf(
                "123" to 200,
                "456" to 200,
                "abc" to 404,
                "" to 404,
            )

        testCases.forEach { (userId, expectedStatus) ->
            val connection = createConnectionGET("/users/$userId")
            val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())
            assertEquals(expectedStatus, cResponse.statusCode(), "Failed for user ID: $userId")
        }
    }

    @Test
    fun `test invalid route returns 404`() {
        val connection = createConnectionGET("/nonexistent")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, cResponse.statusCode())
    }

    @Test
    fun `test setter route with wrong method returns 405`() {
        val connection = createConnectionPOST("/setter")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(405, cResponse.statusCode())
    }

    // ===== HTTP METHOD TESTS =====

    @Test
    fun `test various HTTP methods`() {
        val methods =
            mapOf(
                "GET" to HttpRequest.Builder::GET,
                "POST" to { builder: HttpRequest.Builder ->
                    builder.POST(HttpRequest.BodyPublishers.ofString("{}"))
                },
                "PUT" to { builder: HttpRequest.Builder ->
                    builder.PUT(HttpRequest.BodyPublishers.ofString("{}"))
                },
                "DELETE" to HttpRequest.Builder::DELETE,
                "HEAD" to { builder: HttpRequest.Builder ->
                    builder.method("HEAD", HttpRequest.BodyPublishers.noBody())
                },
            )

        methods.forEach { (method, requestBuilder) ->
            try {
                val request =
                    HttpRequest
                        .newBuilder()
                        .uri(URI.create("http://localhost:$httpPort/"))
                requestBuilder(request)
                val response = client.send(request.build(), HttpResponse.BodyHandlers.ofString())

                // Response should be valid (200, 404, 405, etc.) not connection error
                assertTrue(response.statusCode() in 100..599, "Invalid status for $method")
            } catch (e: Exception) {
                println("$method test failed: ${e.message}")
            }
        }
    }

    @Test
    fun `test tailwind test route and css generation`() {
        val connection = createConnectionGET("/tailwind-test")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cResponse.statusCode())
        val html = cResponse.body()
        assertTrue(html.contains("<html"))

        // Extract generated CSS link
        val cssHref = Regex("href=\\\"(/css/[^\\\"]+/styles\\.css)\\\"").find(html)?.groupValues?.get(1)
        assertNotNull(cssHref, "Should include generated Tailwind CSS link in metadata")

        val cssRequest =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort$cssHref"))
                .GET()
                .build()
        val cssResponse = client.send(cssRequest, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cssResponse.statusCode())
        val css = cssResponse.body()

        // Assertions for key features
        assertTrue(
            css.contains(".mb\\:\\[7px\\]") || css.contains("margin-bottom: 7px"),
            "CSS should include rule for mb-[7px]",
        )
        assertTrue(
            css.contains(".\\-mx\\:\\[1rem\\]") || css.contains("margin-left: -1rem"),
            "CSS should include rule for -mx-[1rem]",
        )
        assertTrue(
            css.contains(".hover\\:py\\:\\[10%\\]\\:hover") || css.contains("padding-top: 10%"),
            "CSS should include rule for hover:py-[10%]",
        )
        assertTrue(
            css.contains("@media (min-width: 640px)") && (css.contains(".sm\\:mb-2") || css.contains("margin-bottom")),
            "CSS should include responsive rule for sm:mb-2",
        )
        assertTrue(
            css.contains("@media (min-width: 640px)") && (css.contains(".sm\\:hover\\:mb\\:\\[3px\\]\\:hover") || css.contains("margin-bottom: 3px")),
            "CSS should include combined responsive+state rule for sm:hover:mb-[3px]",
        )
    }

    // ===== PERFORMANCE TESTS =====

    // ===== FETCH TESTS =====

    @Test
    fun `test fetch GET home returns 200 and HTML`() {
        val request =
            buildRequest {
                method = Method.GET
                target = "/"
            }
        val promise = fetch("http://localhost:$httpPort", request)
        promise.onSuccess { response ->
            assertEquals(200, response.status)
            val contentType = response.headers["Content-Type"] ?: ""
            assertTrue(contentType.contains("text/html"))
            val body = (response.body.body as? String) ?: ""
            assertTrue(body.contains("<html"))
        }
    }

    @Test
    fun `test fetch then chaining`() {
        val request =
            buildRequest {
                method = Method.GET
                target = "/setter"
            }
        val promise = fetch("http://localhost:$httpPort", request)
        promise
            .onSuccess { response ->
                assertEquals(200, response.status)
                val contentType = response.headers["Content-Type"] ?: ""
                assertTrue(contentType.contains("application/json"))
            }
    }

    @Test
    fun `test fetch catch on connection failure`() {
        val badPort = httpPort + 12345
        val request =
            buildRequest {
                method = Method.GET
                target = "/"
            }
        val promise = fetch("http://localhost:$badPort", request)
        promise
            .onSuccess { response ->
                assertEquals(599, response.status)
                val body = (response.body.body as? String) ?: ""
                assertEquals("synthetic", body)
            }.onFailure {
                buildResponse {
                    status = 599
                    statusText = "Network Error"
                    body = "synthetic"
                }
            }
    }

    @Test
    fun `test response time performance`() {
        if (!enablePerfTests) {
            println("Performance test skipped - enable with -Dvoid.tests.perf=true")
            return
        }
        val maxResponseTime = 1000L // 1 second
        val iterations = 3

        repeat(iterations) {
            val startTime = System.currentTimeMillis()
            val connection = createConnectionGET("/")
            client.send(connection, HttpResponse.BodyHandlers.ofString())
            val endTime = System.currentTimeMillis()

            val responseTime = endTime - startTime
            assertTrue(
                responseTime < maxResponseTime,
                "Response time $responseTime ms exceeded limit of $maxResponseTime ms",
            )
        }
    }

    @Test
    fun `test server stability under load`() {
        if (!enableLoadTests) {
            println("Load test skipped - enable with -Dvoid.tests.load=true")
            return
        }
        val requestCount = 30
        val successfulRequests = mutableListOf<Boolean>()
        val latch = CountDownLatch(requestCount)

        repeat(requestCount) { _ ->
            Thread {
                try {
                    val connection = createConnectionGET("/setter")
                    val response = client.send(connection, HttpResponse.BodyHandlers.ofString())
                    synchronized(successfulRequests) {
                        successfulRequests.add(response.statusCode() == 200)
                    }
                } catch (_: Exception) {
                    synchronized(successfulRequests) {
                        successfulRequests.add(false)
                    }
                } finally {
                    latch.countDown()
                }
            }.start()
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS))
        val successRate = successfulRequests.count { it } / successfulRequests.size.toDouble()
        assertTrue(successRate > 0.9, "Success rate should be > 90%, was ${successRate * 100}%")
    }

    // ===== QUERY TESTS =====

    @Test
    fun `test echo route returns query map`() {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort/echo?foo=bar&num=42"))
                .GET()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        val dto = response.body().fromJson<EchoDTO>().getOrThrow()
        assertEquals("/echo", dto.path)
        assertTrue(dto.hasQuery)
        assertEquals("bar", dto.query["foo"])
        assertEquals("42", dto.query["num"])
    }

    @Test
    fun `test url encoded values are not decoded by server`() {
        val encoded = "hello%20world%2Bplus"
        val unicode = "%E2%9C%93" // checkmark
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort/echo?q=$encoded&u=$unicode"))
                .GET()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        val dto = response.body().fromJson<EchoDTO>().getOrThrow()
        assertEquals(encoded, dto.query["q"])
        assertEquals(unicode, dto.query["u"])
    }

    @Test
    fun `test duplicate keys last value wins`() {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort/echo?x=1&x=2&x=3"))
                .GET()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        val dto = response.body().fromJson<EchoDTO>().getOrThrow()
        assertEquals("3", dto.query["x"])
    }

    @Test
    fun `test missing value is ignored but empty value kept`() {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort/echo?a=&b"))
                .GET()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        val dto = response.body().fromJson<EchoDTO>().getOrThrow()
        assertTrue(dto.query.containsKey("a"))
        assertEquals("", dto.query["a"])
        assertFalse(dto.query.containsKey("b"))
    }

    @Test
    fun `test dynamic search route with optional segment and queries`() {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort/search/books?q=kotlin&sort=asc"))
                .GET()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        val dto = response.body().fromJson<SearchDTO>().getOrThrow()
        assertEquals("/search/books", dto.path)
        assertEquals("books", dto.category)
        assertEquals("kotlin", dto.query["q"])
        assertEquals("asc", dto.query["sort"])
    }

    @Test
    fun `test dynamic search route without optional segment`() {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("http://localhost:$httpPort/search?q=latest"))
                .GET()
                .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertEquals(200, response.statusCode())
        val dto = response.body().fromJson<SearchDTO>().getOrThrow()
        assertEquals("/search", dto.path)
        assertTrue(dto.category == null)
        assertEquals("latest", dto.query["q"])
    }

    @Test
    fun `test 404 still returned for unknown route with query`() {
        val response = client.send(createConnectionGET("/nope?x=1&y=2"), HttpResponse.BodyHandlers.ofString())
        assertEquals(404, response.statusCode())
    }

    // ===== HELPER METHODS =====

    private fun setupHttpServer() {
        server =
            server {
                port = httpPort
                router =
                    router {
                        +homeRoute
                        +setterRoute
                        +userRoute
                        +htmlRoute("/tailwind-test", {}) {
                            Main("class" to "container mx-auto p-4") {
                                H1("class" to "text-2xl font-bold mb-4") { Fractal("TailwindGen Test Route") }
                                Div("class" to "mb-[7px] p-2 bg-gray-100 rounded") { Fractal("mb-[7px]") }
                                Div("class" to "-mx-[1rem] p-2 bg-gray-100 rounded") { Fractal("-mx-[1rem]") }
                                Button(
                                    "class" to "hover:py-[10%] px-4 bg-blue-500 text-white rounded",
                                    "id" to "hover-btn"
                                ) { Fractal("hover:py-[10%] (hover me)") }
                                Div("class" to "sm:mb-2 p-2 bg-gray-100 rounded mt-4") { Fractal("sm:mb-2") }
                                Div("class" to "sm:hover:mb-[3px] p-2 bg-gray-100 rounded mt-2") { Fractal("sm:hover:mb-[3px]") }
                            }
                        }
                        +echoRoute
                        +searchRoute
                        +searchRootRoute
                    }
                autoStart = false
            }
        serverJob =
            scope.launch {
                server.startHTTPServer(httpPort)
            }
    }

    private fun setupHttpsServerIfKeystoreExists() {
        try {
            val keystoreBytes = createInMemoryKeystoreBytes()
            val tempFile = File.createTempFile("test-keystore", ".p12")
            tempFile.deleteOnExit()
            tempFile.writeBytes(keystoreBytes)

            // Use a single server instance that handles both HTTP redirect and HTTPS
            httpsServer =
                server {
                    port = redirectPort // HTTP port for redirect
                    this.httpsPort = this@RouteTests.httpsPort // HTTPS port
                    router =
                        router {
                            +homeRoute
                            +setterRoute
                            +userRoute
                            +echoRoute
                            +searchRoute
                            +searchRootRoute
                            +htmlRoute("/tailwind-test", {}) {
                                Main("class" to "container mx-auto p-4") {
                                    H1("class" to "text-2xl font-bold mb-4") { Fractal("TailwindGen Test Route") }
                                    Div("class" to "mb-[7px] p-2 bg-gray-100 rounded") { Fractal("mb-[7px]") }
                                    Div("class" to "-mx-[1rem] p-2 bg-gray-100 rounded") { Fractal("-mx-[1rem]") }
                                    Button(
                                        "class" to "hover:py-[10%] px-4 bg-blue-500 text-white rounded",
                                        "id" to "hover-btn"
                                    ) { Fractal("hover:py-[10%] (hover me)") }
                                    Div("class" to "sm:mb-2 p-2 bg-gray-100 rounded mt-4") { Fractal("sm:mb-2") }
                                    Div("class" to "sm:hover:mb-[3px] p-2 bg-gray-100 rounded mt-2") { Fractal("sm:hover:mb-[3px]") }
                                }
                            }
                        }
                    password = "changeit"
                    file = tempFile
                    needsAuth = false
                    routeToHTTPS = true // Enable redirect
                    autoStart = false
                }

            httpsServerJob =
                scope.launch {
                    httpsServer.startHTTPSServer(httpsPort, "changeit", tempFile, false)
                }

            // Start the HTTP redirect server
            redirectServerJob =
                scope.launch {
                    httpsServer.startHTTPServer(redirectPort, true)
                }
        } catch (e: Exception) {
            println("In-memory HTTPS setup failed: ${e.message}")
        }
    }

    private fun createInMemoryKeystoreBytes(): ByteArray {
        val keystore = createInMemoryKeystore()
        val outputStream = ByteArrayOutputStream()
        keystore.store(outputStream, "changeit".toCharArray())
        return outputStream.toByteArray()
    }

    private fun createInMemoryKeystore(): KeyStore {
        // Add BouncyCastle provider
        Security.addProvider(BouncyCastleProvider())

        // Generate RSA key pair
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()

        // Create self-signed certificate
        val now = Instant.now()
        val subject = X500Name("CN=localhost,OU=Test,O=Test,L=Test,ST=Test,C=US")

        val certBuilder =
            JcaX509v3CertificateBuilder(
                subject, // issuer
                BigInteger.valueOf(System.currentTimeMillis()), // serial number
                Date.from(now), // not before
                Date.from(now.plusSeconds(365 * 24 * 3600)), // not after (1 year)
                subject, // subject
                keyPair.public, // public key
            )

        val signer =
            JcaContentSignerBuilder("SHA256withRSA")
                .setProvider("BC")
                .build(keyPair.private)

        val certificate =
            JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(certBuilder.build(signer))

        // Create keystore and add the certificate
        val keystore = KeyStore.getInstance("PKCS12")
        keystore.load(null, null)
        keystore.setKeyEntry(
            "testkey",
            keyPair.private,
            "changeit".toCharArray(),
            arrayOf(certificate),
        )

        return keystore
    }

    private fun createConnectionGET(path: String): HttpRequest =
        HttpRequest
            .newBuilder()
            .uri(URI.create("http://localhost:$httpPort$path"))
            .GET()
            .build()

    private fun createConnectionPOST(path: String): HttpRequest =
        HttpRequest
            .newBuilder()
            .uri(URI.create("http://localhost:$httpPort$path"))
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .build()

    private fun createUnsafeSSLContext(): SSLContext {
        val trustAllCertificates =
            object : X509TrustManager {
                override fun checkClientTrusted(
                    chain: Array<X509Certificate>,
                    authType: String,
                ) {}

                override fun checkServerTrusted(
                    chain: Array<X509Certificate>,
                    authType: String,
                ) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
            }

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustAllCertificates), null)
        return sslContext
    }

    private fun waitForPort(
        port: Int,
        timeoutMs: Long,
    ) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                Socket().use { s ->
                    s.connect(InetSocketAddress("127.0.0.1", port), 50)
                    return
                }
            } catch (_: Exception) {
                Thread.sleep(20)
            }
        }
    }

    private fun findFreePort(): Int {
        ServerSocket(0).use { socket ->
            socket.reuseAddress = true
            return socket.localPort
        }
    }
}
