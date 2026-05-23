package io.voidx

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.RequestDTO
import io.voidx.dto.buildResponse
import io.voidx.dto.headers
import io.voidx.dto.writeHTTP
import io.voidx.exception.NotEnoughCarriers
import io.voidx.router.Router
import io.voidx.util.toResult
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
import kotlin.time.Duration.Companion.milliseconds

/**
 * Minimal HTTP/HTTPS server used by Void to serve routes from a [Router].
 *
 * Lifecycle:
 * - Construct with a [Router]. During initialization, module hooks (see [io.voidx.util.ModuleInit])
 *   are executed and any registered HTML integration is applied to existing routes.
 * - Call [startHTTPServer] and/or [startHTTPSServer] to accept connections.
 * - Each connection is handled on a coroutine via [Socket.handle].
 *
 * @param httpVersion HTTP version used when writing responses.
 */
class Server(
    private val router: Router,
    val httpVersion: Number = 1.1,
) {
    private lateinit var socket: ServerSocket
    private lateinit var httpsSocket: SSLServerSocket
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val keystore: KeyStore = KeyStore.getInstance("PKCS12")
    private val context: SSLContext = SSLContext.getInstance("TLS")
    private var isHTTPSOn = false

    var useCarriers = false

    /** Callback invoked when a server socket (HTTP or HTTPS) throws while starting or accepting. */
    var onServerSocketError: (Exception) -> Unit = {
        it.printStackTrace()
    }

    /** Callback invoked when a server socket is about to close; allows custom cleanup logic. */
    var onServerSocketClose: (ServerSocket) -> Unit = {
        it.close()
    }

    /**
     * Stops the server by closing HTTP and HTTPS sockets and cancelling the coroutine scope.
     */
    fun stop() {
        if (::socket.isInitialized) {
            socket.close()
        }
        if (::httpsSocket.isInitialized) {
            httpsSocket.close()
        }
        scope.cancel()
    }

    /**
     * Starts an HTTP server bound to the specified port.
     *
     * @param routeToHTTPS If true, new HTTP connections wait for HTTPS to become available and receive a 301 redirect to the HTTPS host.
     * @throws NotEnoughCarriers when `useCarriers` is enabled and the server cannot start.
     */
    fun startHTTPServer(
        port: Int,
        routeToHTTPS: Boolean = false,
    ) {
        if (useCarriers) {
            throw NotEnoughCarriers()
        }
        Thread {
            try {
                Bootstrap.fireBeforeServerStart(Bootstrap.ServerKind.HTTP, port)
                socket = ServerSocket(port)
                Bootstrap.fireAfterServerStart(Bootstrap.ServerKind.HTTP, port)
                while (socket.isBound && !socket.isClosed) {
                    val client =
                        try {
                            socket.accept()
                        } catch (e: Exception) {
                            if (socket.isClosed) break else throw e
                        }
                    if (routeToHTTPS) {
                        scope.launch {
                            waitForHTTPSAndRedirect(client)
                        }
                    } else {
                        scope.launch {
                            try {
                                client.handle(this@Server.httpVersion, router)
                            } catch (e: Exception) {
                                client.error(this@Server.httpVersion, router, e)
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
                Bootstrap.fireShutdown()
            }
        }.start()
    }

    /**
     * Start an HTTPS server that accepts TLS connections and routes requests through the configured router.
     *
     * @param port TCP port to listen for HTTPS connections.
     * @param password Password for the PKCS12 keystore.
     * @param file PKCS12 keystore file containing the server certificate and key.
     * @param needsAuth If `true`, require client certificate authentication.
     * @throws NotEnoughCarriers If carrier mode is enabled and there are not enough carriers to start the server.
     */
    fun startHTTPSServer(
        port: Int,
        password: String,
        file: File,
        needsAuth: Boolean,
    ) {
        if (useCarriers) {
            throw NotEnoughCarriers()
        }
        Thread {
            val paswd = password.toCharArray()
            try {
                FileInputStream(file).use { keystore.load(it, paswd) }

                val kmf = KeyManagerFactory.getInstance("SunX509")
                kmf.init(keystore, paswd)

                context.init(kmf.keyManagers, null, SecureRandom())
                val factory = context.serverSocketFactory

                Bootstrap.fireBeforeServerStart(Bootstrap.ServerKind.HTTPS, port)
                httpsSocket = factory.createServerSocket(port) as SSLServerSocket
                isHTTPSOn = true
                httpsSocket.needClientAuth = needsAuth
                Bootstrap.fireAfterServerStart(Bootstrap.ServerKind.HTTPS, port)
                while (httpsSocket.isBound && !httpsSocket.isClosed) {
                    val client =
                        try {
                            httpsSocket.accept() as SSLSocket
                        } catch (e: Exception) {
                            if (httpsSocket.isClosed) break else throw e
                        }
                    client.startHandshake()
                    scope.launch {
                        try {
                            client.handle(this@Server.httpVersion, router)
                        } catch (e: Exception) {
                            client.error(this@Server.httpVersion, router, e)
                        }
                    }
                }
            } catch (e: Exception) {
                onServerSocketError(e)
            } finally {
                if (::httpsSocket.isInitialized) {
                    onServerSocketClose(httpsSocket)
                }
                Bootstrap.fireShutdown()
            }
        }.start()
    }

    /** Returns true if the HTTPS server socket is initialized, bound, open, and HTTPS mode is enabled. */
    fun isHTTPSServerRunning(): Boolean = isHTTPSOn && ::httpsSocket.isInitialized && httpsSocket.isBound && !httpsSocket.isClosed

    /**
     * Waits until the HTTPS server is available, then writes a 301 redirect to the client and closes the socket.
     *
     * Repeatedly checks the server HTTPS status and, once available, sends a "Moved Permanently" response
     * with a Location header pointing to the client's host on the HTTPS scheme. Ensures the client socket
     * is closed on error or after the redirect.
     *
     * @param client The accepted HTTP client socket to which the redirect response will be written.
     */
    private suspend fun waitForHTTPSAndRedirect(client: Socket) {
        try {
            // Keep checking until HTTPS is available
            while (!isHTTPSServerRunning()) {
                delay(50.milliseconds)
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
 * Creates and starts an HTTP Server bound to the given port and configured with the provided router DSL.
 *
 * @param port TCP port to bind the HTTP server to.
 * @param builder DSL block that configures the server's Router.
 * @return The started Server instance.
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
 * Handles an incoming connection [Socket] by parsing the request and routing it through the [router].
 * The socket is automatically closed after processing.
 *
 * @param version The HTTP version to use for the response.
 * @param router The [Router] to use for routing the request.
 */
fun Socket.handle(
    version: Number,
    router: Router,
) {
    try {
        val request =
            RequestDTO.parse(
                inputStream = this.getInputStream(),
            )
        router.route(
            requestDTO = request,
            client = this,
            version = version,
        )
    } catch (e: Exception) {
        this.error(version, router, e)
    } finally {
        this.close()
    }
}

/**
 * Process an exception for this socket connection, optionally sending a middleware-provided response or delegating to the router's error handler.
 *
 * If the router's middleware provides a response for the exception, that response is written to the socket and handling ends. Otherwise the router's error handler is invoked. The socket is closed after handling; any exception thrown by the router's error handler is caught and its stack trace is printed.
 *
 * @param version The HTTP version to use when writing a response.
 * @param router The router responsible for producing or handling error responses.
 * @param exception The exception that occurred during request processing.
 */
fun Socket.error(
    version: Number,
    router: Router,
    exception: Exception,
) {
    val response = router.middlewareProcessBefore(exception.toResult())
    if (response != null) {
        router.middlewareProcessAfter(response.toResult())
        this.getOutputStream().writeHTTP(
            response = response,
            version = version,
        )
        return
    }
    try {
        router.error(this, exception, version)
    } catch (error: Exception) {
        error.printStackTrace()
    } finally {
        this.close()
    }
}
