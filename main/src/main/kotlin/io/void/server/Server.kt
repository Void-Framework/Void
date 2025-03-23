package io.void.server

import io.void.clienthandler.ClientHandler
import io.void.dto.ResponseDTO
import io.void.http.builder.HTTPBuilder
import io.void.router.Router
import io.void.server.exception.HTTPSNotOnException
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket

class Server(private val router: Router) {

    private lateinit var socket: ServerSocket
    private val executorService: ExecutorService = Executors.newCachedThreadPool()
    private val keystore: KeyStore = KeyStore.getInstance("PKCS12")
    private val context: SSLContext = SSLContext.getInstance("TLS")
    private var isHTTPSOn = false
    private val builder: HTTPBuilder = HTTPBuilder()

    fun startHTTPServer(port: Int, routeToHTTPS: Boolean = false) {
        Thread {
            try {
                socket = ServerSocket(port)
                while (socket.isBound) {
                    val client = socket.accept()
                    if (routeToHTTPS) {
                        if (isHTTPSOn) {
                            builder.build(
                                response = ResponseDTO(
                                    status = 301,
                                    statusText = "Moved Permanently",
                                    headers = mutableMapOf("Location" to "https://${client.inetAddress.hostName}"),
                                    body = ""
                                ),
                                outputStream = client.getOutputStream()
                            )
                        } else {
                            throw HTTPSNotOnException()
                        }
                    } else {
                        executorService.submit {
                            try {
                                ClientHandler(client).setRouter(router = this.router).start()
                            } catch (e: Exception) {
                                ClientHandler(client).error(e)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                socket.close()
            }
        }.start()
    }

    fun startHTTPSServer(port: Int, password: String, file: File, needsAuth: Boolean) {
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
                executorService.submit {
                    try {
                        ClientHandler(client).setRouter(router = this.router).start()
                    } catch (e: Exception) {
                        ClientHandler(client).error(e)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}