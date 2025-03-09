package io.void.server

import io.void.clienthandler.ClientHandler
import io.void.router.Router
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.util.concurrent.Executors
import java.security.KeyStore
import java.security.SecureRandom
import java.util.concurrent.ExecutorService
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket

class Server(private val router: Router) {

    private lateinit var socket: ServerSocket
    private val executorService: ExecutorService = Executors.newCachedThreadPool()
    private val keystore: KeyStore = KeyStore.getInstance("PKCS12")
    private val context: SSLContext = SSLContext.getInstance("TLS")

    fun startHTTPServer(port: Int) {
        Thread {
            try {
                socket = ServerSocket(port)
                while (socket.isBound) {
                    val client = socket.accept()
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
            } finally {
                socket.close()
            }
        }.start()
    }

    fun startHTTPSServer(port: Int, password: String, file: File, needsAuth: Boolean) {
        val paswd = password.toCharArray()
        val fis: FileInputStream
        try {
            fis = FileInputStream(file)
            keystore.load(fis, paswd)

            val kmf = KeyManagerFactory.getInstance("SunX509")
            kmf.init(keystore, paswd)

            context.init(kmf.keyManagers, null, SecureRandom())
            val factory = context.serverSocketFactory

            val server = factory.createServerSocket(port) as SSLServerSocket
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