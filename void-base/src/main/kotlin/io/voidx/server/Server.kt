package io.voidx.server

import io.voidx.clienthandler.ClientHandler
import io.voidx.dto.http.buildResponse
import io.voidx.dto.http.headers
import io.voidx.dto.http.writeHTTP
import io.voidx.router.Router
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket

/**
 * Minimal HTTP/HTTPS server used by Void to serve routes from a [Router].
 *
 * Lifecycle:
 * - Construct with a [Router]. During initialization, module hooks (see [io.voidx.util.ModuleInit])
 *   are executed and any registered HTML integration is applied to existing routes.
 * - Call [startHTTPServer] and/or [startHTTPSServer] to accept connections.
 * - Each connection is handled on a coroutine via [io.voidx.clienthandler.ClientHandler].
 *
 * @param httpVersion HTTP version used when writing responses.
 */
class Server internal constructor(
    private val router: Router,
    val httpVersion: Number = 1.1,
) {
    private lateinit var socket: ServerSocket
    private lateinit var httpsSocket: SSLServerSocket
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val keystore: KeyStore = KeyStore.getInstance("PKCS12")
    private val context: SSLContext = SSLContext.getInstance("TLS")
    var isHTTPSOn = false

    /** Callback invoked when a server socket (HTTP or HTTPS) throws while starting or accepting. */
    var onServerSocketError: (Exception) -> Unit = {
        it.printStackTrace()
    }

    /** Callback invoked when a server socket is about to close; allows custom cleanup logic. */
    var onServerSocketClose: (ServerSocket) -> Unit = {
        it.close()
    }

    /**
     * Starts the HTTP server on the given [port].
     *
     * @param routeToHTTPS If true, new HTTP connections will wait until HTTPS is ready and then
     * redirect the client to the HTTPS host using a 301 response.
     */
    fun startHTTPServer(
        port: Int,
        routeToHTTPS: Boolean = false,
    ) {
        Thread {
            try {
                socket = ServerSocket(port)
                while (socket.isBound) {
                    val client = socket.accept()
                    if (routeToHTTPS) {
                        scope.launch {
                            waitForHTTPSAndRedirect(client)
                        }
                    } else {
                        scope.launch {
                            try {
                                client.handle(this@Server, router)
                            } catch (e: Exception) {
                                client.error(this@Server, router, e)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                onServerSocketError(e)
            } finally {
                if (::socket.isInitialized) {
                    onServerSocketClose(socket)
                }
            }
        }.start()
    }

    /**
     * Starts the HTTPS server on the given [port] using the provided PKCS12 keystore [file].
     *
     * @param password Keystore password.
     * @param needsAuth Whether to require client certificate authentication.
     */
    fun startHTTPSServer(
        port: Int,
        password: String,
        file: File,
        needsAuth: Boolean,
    ) {
        Thread {
            val paswd = password.toCharArray()
            try {
                FileInputStream(file).use { keystore.load(it, paswd) }

                val kmf = KeyManagerFactory.getInstance("SunX509")
                kmf.init(keystore, paswd)

                context.init(kmf.keyManagers, null, SecureRandom())
                val factory = context.serverSocketFactory

                httpsSocket = factory.createServerSocket(port) as SSLServerSocket
                isHTTPSOn = true
                httpsSocket.needClientAuth = needsAuth
                while (httpsSocket.isBound) {
                    val client = httpsSocket.accept() as SSLSocket
                    client.startHandshake()
                    scope.launch {
                        try {
                            client.handle(this@Server, router)
                        } catch (e: Exception) {
                            client.error(this@Server, router, e)
                        }
                    }
                }
            } catch (e: Exception) {
                onServerSocketError(e)
            } finally {
                if (::httpsSocket.isInitialized) {
                    onServerSocketClose(httpsSocket)
                }
            }
        }.start()
    }

    /** Returns true if the HTTPS server socket is initialized, bound, open, and HTTPS mode is enabled. */
    fun isHTTPSServerRunning(): Boolean = isHTTPSOn && ::httpsSocket.isInitialized && httpsSocket.isBound && !httpsSocket.isClosed

    private suspend fun waitForHTTPSAndRedirect(client: Socket) {
        try {
            // Keep checking until HTTPS is available
            while (!isHTTPSServerRunning()) {
                delay(50)
            }

            // Send redirect once HTTPS is ready
            withContext(Dispatchers.IO) {
                client.getOutputStream().writeHTTP(
                    response =
                        buildResponse {
                            status = 301
                            statusText = "Moved Permanently"
                            headers {
                                put("Location", "https://${client.inetAddress.hostName}")
                            }
                            body = ""
                        },
                    version = httpVersion,
                )
            }
        } catch (e: Exception) {
            withContext(Dispatchers.IO) {
                runCatching { client.close() }
            }
        } finally {
            withContext(Dispatchers.IO) {
                runCatching { client.close() }
            }
        }
    }
}

/**
 * Builder for configuring and creating a [Server].
 *
 * Defaults: port=8080, httpsPort=8081, httpVersion=1.1. If a keystore [file] is provided,
 * HTTPS will be started; optionally set [routeToHTTPS] to also start HTTP that redirects to HTTPS.
 */
class ServerBuilder {
    var port: Int = 8080
    var httpsPort: Int = 8081
    var httpVersion: Number = 1.1
    lateinit var router: Router
    var password: String? = null
    var file: File? = null
    var needsAuth: Boolean? = null
    var routeToHTTPS: Boolean = false
    var onServerSocketError: (Exception) -> Unit = {
        it.printStackTrace()
    }
    var onServerSocketClose: (ServerSocket) -> Unit = {
        it.close()
    }
    var autoStart: Boolean = true

    /**
     * Builds a [Server] instance from the configured properties. If [autoStart] is true,
     * this will start the appropriate server(s):
     * - HTTPS if [file] (PKCS12 keystore) is provided, and optionally HTTP for redirect when [routeToHTTPS] is true.
     * - Otherwise, a plain HTTP server on [port].
     */
    fun build(): Server {
        val server = Server(router, httpVersion)
        server.onServerSocketError = onServerSocketError
        server.onServerSocketClose = onServerSocketClose
        if (autoStart) {
            if (file != null) {
                server.startHTTPSServer(httpsPort, password!!, file!!, needsAuth!!)
                if (routeToHTTPS) server.startHTTPServer(port, true)
            } else {
                server.startHTTPServer(port)
            }
        }
        return server
    }
}

/**
 * DSL helper to create and optionally start a [Server] using a [ServerBuilder] configuration block.
 */
fun server(builder: ServerBuilder.() -> Unit): Server {
    val sBuilder = ServerBuilder()
    sBuilder.builder()
    return sBuilder.build()
}

/**
 * Convenience function to spin up a simple HTTP server quickly on [port] with routes defined in [builder].
 */
fun simpleServer(
    port: Int = 8080,
    builder: Router.() -> Unit,
): Server {
    val router = Router()
    router.builder()
    val server = Server(router)
    server.startHTTPServer(port)
    return server
}

/**
 * Handles an incoming connection [Socket] by delegating processing to [ClientHandler].
 */
fun Socket.handle(
    server: Server,
    router: Router,
) {
    ClientHandler(this, server, router).start()
}

/**
 * Handles an error that occurred while processing a connection by delegating to [ClientHandler.error].
 *
 * @param exception The exception that was thrown during request processing.
 */
fun Socket.error(
    server: Server,
    router: Router,
    exception: Exception,
) {
    ClientHandler(this, server, router).error(exception)
}
