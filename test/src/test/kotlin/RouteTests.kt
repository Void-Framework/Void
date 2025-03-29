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
import java.net.HttpURLConnection
import java.net.URL
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
        
        // Wait for server to start
        TimeUnit.SECONDS.sleep(2)
    }

    @AfterAll
    fun tearDown() {
        serverJob?.cancel()
        scope.cancel()
    }

    @Test
    fun `test home route returns 200 and valid HTML`() {
        val connection = URL("http://localhost:$port/").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        assertEquals(200, connection.responseCode)
        assertTrue(connection.contentType.contains("text/html"))
        
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        assertTrue(response.contains("<html"))
        assertTrue(response.contains("</html>"))
    }

    @Test
    fun `test setter route returns valid JSON`() {
        val connection = URL("http://localhost:$port/setter").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        assertEquals(200, connection.responseCode)
        assertTrue(connection.contentType.contains("application/json"))
        
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)
        
        // Validate JSON structure
        assertEquals("Jade", json.getString("name"))
        assertEquals(20, json.getInt("age"))
        assertTrue(json.getBoolean("isStudent"))
        
        // Test nested structures
        val meta = json.getJSONObject("meta")
        assertTrue(meta.getBoolean("registered"))
        
        val languages = meta.getJSONArray("languages")
        assertTrue(languages.toString().contains("Kotlin"))
    }

    @Test
    fun `test dynamic user route returns correct user data`() {
        val userId = "123"
        val connection = URL("http://localhost:$port/users/$userId").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        assertEquals(200, connection.responseCode)
        assertTrue(connection.contentType.contains("application/json"))
        
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)
        
        assertEquals(userId, json.getString("id"))
        assertNotNull(json.getString("name"))
        assertNotNull(json.getString("email"))
    }

    @Test
    fun `test dynamic user route with invalid ID returns 404`() {
        val connection = URL("http://localhost:$port/users/invalid").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        assertEquals(404, connection.responseCode)
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
            val connection = URL("http://localhost:$port/users/$userId").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            assertEquals(expectedStatus, connection.responseCode, "Failed for user ID: $userId")
        }
    }

    @Test
    fun `test invalid route returns 404`() {
        val connection = URL("http://localhost:$port/nonexistent").openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        
        assertEquals(404, connection.responseCode)
    }

    @Test
    fun `test setter route with wrong method returns 405`() {
        val connection = URL("http://localhost:$port/setter").openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        
        assertEquals(405, connection.responseCode)
    }

    @Test
    fun `test cached route returns same content`() {
        val url = URL("http://localhost:$port/")
        
        // First request
        val response1 = (url.openConnection() as HttpURLConnection).let {
            it.requestMethod = "GET"
            it.inputStream.bufferedReader().use { reader -> reader.readText() }
        }
        
        // Second request
        val response2 = (url.openConnection() as HttpURLConnection).let {
            it.requestMethod = "GET"
            it.inputStream.bufferedReader().use { reader -> reader.readText() }
        }
        
        assertEquals(response1, response2, "Cached content should be identical")
    }
}