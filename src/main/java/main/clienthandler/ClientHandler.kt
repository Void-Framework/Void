package main.java.main.ClientHandler

import main.router.Router
import main.java.main.HTTP.Parser.HTTPParser
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class ClientHandler(private val client: Socket) {

    fun start() {
        try {
            val request = HTTPParser().parse(client.getInputStream())
            Router().route(request, client)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            client.close()
        }
    }
}