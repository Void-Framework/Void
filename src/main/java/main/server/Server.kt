package main.java.main.Server

import main.java.main.ClientHandler.ClientHandler
import main.router.Route
import main.router.Router
import java.io.File
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Server {

    private lateinit var socket: ServerSocket
    val executorService = Executors.newCachedThreadPool()

    fun startServer(port: Int) {
        Thread {
            try {
                socket = ServerSocket(port)
                while (socket.isBound) {
                    val client = socket.accept()
                    executorService.submit {
                        ClientHandler(client).start()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                socket.close()
            }
        }.start()
    }
}