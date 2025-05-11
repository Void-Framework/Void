package test

import io.jadiefication.routes.home.HomeRoute
import io.jadiefication.routes.setter.SetterRoute
import io.jadiefication.routes.user.UserRoute
import io.void.router.Router
import io.void.server.Server
import kotlinx.coroutines.*
import org.json.JSONObject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RouteTests {
    private lateinit var server: Server
    private val port = 8081  // Different port for testing
    private val scope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null
    private val client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1) // Forces HTTP/2
        .build()

    @BeforeAll
    fun setup() {
        val router = Router().addRoutes(listOf(
            HomeRoute(),
            SetterRoute(),
            UserRoute() // Add the dynamic route
        ))
        server = Server(router = router)

        serverJob = scope.launch {
            server.startHTTPServer(port = port)
        }
        
        // Wait for server to start and be ready
        TimeUnit.SECONDS.sleep(3)
    }

    @AfterAll
    fun tearDown() {
        runBlocking {
            serverJob?.cancelAndJoin()
            scope.cancel("Cancelled")
        }
    }
    private fun createConnectionGET(path: String): HttpRequest {
        val connection = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port$path"))
            .GET()
            .build()
        return connection
    }

    private fun createConnectionPOST(path: String): HttpRequest {
        val connection = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:$port$path"))
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .build()
        return connection
    }

    @Test
    fun `test home route returns 200 and valid HTML`() {
        val connection = createConnectionGET("/")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cResponse.statusCode())
        assertTrue(cResponse.headers().allValues("Content-Type").contains("text/html"))
        
        val response = cResponse.body()
        assertTrue(response.contains("<html"))
        assertTrue(response.contains("</html>"))
    }

    @Test
    fun `test setter route returns valid JSON`() {
        val connection = createConnectionGET("/setter")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())
        
        assertEquals(200, cResponse.statusCode())
        assertTrue(cResponse.headers().allValues("Content-Type").contains("application/json"))
        
        val response = cResponse.body()
        val json = JSONObject(response)

        assertEquals("Jade", json.getString("name"))
        assertEquals(20, json.getInt("age"))
        assertTrue(json.getBoolean("isStudent"))

        val meta = json.getJSONObject("meta")
        assertTrue(meta.getBoolean("registered"))
        
        val languages = meta.getJSONArray("languages")
        assertTrue(languages.toString().contains("Kotlin"))
    }

    @Test
    fun `test dynamic user route returns correct user data`() {
        val userId = "123"
        val connection = createConnectionGET("/users/$userId")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(200, cResponse.statusCode())
        assertTrue(cResponse.headers().allValues("Content-Type").contains("application/json"))

        val response = cResponse.body()
        val json = JSONObject(response)

        assertEquals(userId, json.getString("id"))
        assertNotNull(json.getString("name"))
        assertNotNull(json.getString("email"))
    }

    @Test
    fun `test dynamic user route with invalid ID returns 404`() {
        val connection = createConnectionGET("/users/invalid")
        val cResponse = client.send(connection, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, cResponse.statusCode())
    }

    @Test
    fun `test dynamic route pattern matching works correctly`() {
        val testCases = listOf(
            "123" to 200,
            "456" to 200,
            "abc" to 404,  // assuming we only accept numeric IDs
            "" to 404
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
}