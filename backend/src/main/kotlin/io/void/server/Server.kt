package io.void.server

import io.void.clienthandler.ClientHandler
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.dto.http.writeHTTP
import io.void.router.Router
import io.void.router.util.MiddlewareTime
import io.void.server.exception.HTTPSNotOnException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.CountDownLatch
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket

class Server internal constructor(
    private val router: Router,
    val httpVersion: Number = 1.1,
) {
    private lateinit var socket: ServerSocket
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val keystore: KeyStore = KeyStore.getInstance("PKCS12")
    private val context: SSLContext = SSLContext.getInstance("TLS")
    var isHTTPSOn = false
    var onServerSocketError: (Exception) -> Unit = {
        it.printStackTrace()
    }
    var onServerSocketClose: (ServerSocket) -> Unit = {
        it.close()
    }

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
                        if (isHTTPSOn) {
                            client.getOutputStream().writeHTTP(
                                response = buildResponse {
                                    status = 301
                                    statusText = "Moved Permanently"
                                    headers {
                                        put("Location", "https://${client.inetAddress.hostName}")
                                    }
                                    body = ""
                                },
                                version = httpVersion
                            )
                        } else {
                            throw HTTPSNotOnException()
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
                onServerSocketClose(socket)
            }
        }.start()
    }

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

                val server = factory.createServerSocket(port) as SSLServerSocket
                isHTTPSOn = true
                server.needClientAuth = needsAuth
                while (server.isBound) {
                    val client = server.accept() as SSLSocket
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
                onServerSocketClose(socket)
            }
        }.start()
    }
}

class ServerBuilder {
    var port: Int = 8080
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

    fun build(): Server {
        val server = Server(router, httpVersion)
        server.onServerSocketError = onServerSocketError
        server.onServerSocketClose = onServerSocketClose
        if (autoStart) {
            if (file != null) {
                server.startHTTPSServer(port, password!!, file!!, needsAuth!!)
                if (routeToHTTPS) server.startHTTPServer(port, true)
            } else {
                server.startHTTPServer(port)
            }
        }
        return server
    }
}

fun server(builder: ServerBuilder.() -> Unit): Server {
    val sBuilder = ServerBuilder()
    sBuilder.builder()
    return sBuilder.build()
}

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

fun Socket.handle(
    server: Server,
    router: Router,
) {
    ClientHandler(this, server, router).start()
}

fun Socket.error(
    server: Server,
    router: Router,
    exception: Exception,
) {
    ClientHandler(this, server, router).error(exception, MiddlewareTime.BEFORE)
}
