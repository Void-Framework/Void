package test

import io.jadiefication.routes.home.HomeRoute
import org.junit.jupiter.api.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.*
import io.jadiefication.routes.setter.SetterRoute
import io.void.router.Router
import io.void.server.Server
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RouteTests {
    private lateinit var server: Server
    private val port = 8081  // Different port for testing
    private val scope = CoroutineScope(Dispatchers.IO)
    private var serverJob: Job? = null

    @BeforeAll
    fun setup() {
        val router = Router().addRoutes(listOf(HomeRoute(), SetterRoute()))
        server = Server(router = router)
        
        serverJob = scope.launch {
            server.startHTTPServer(port)
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
}