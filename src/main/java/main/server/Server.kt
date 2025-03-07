package main.java.main.Server

import main.java.main.ClientHandler.ClientHandler
import main.router.Router
import java.net.ServerSocket
import java.util.concurrent.Executors

class Server(router: Router) {

    private lateinit var socket: ServerSocket
    val executorService = Executors.newCachedThreadPool()

    fun startServer(port: Int) {
        Thread {
            try {
                socket = ServerSocket(port)
                while (socket.isBound) {
                    val client = socket.accept()
                    executorService.submit {
                        try {
                            ClientHandler(client).start()
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
}