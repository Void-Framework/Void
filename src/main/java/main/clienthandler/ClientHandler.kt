package main.java.main.ClientHandler

import main.java.main.HTTP.Parser.HTTPParser
import main.router.Router
import java.net.Socket

class ClientHandler(private val client: Socket) {

    fun start() {
        try {
            val request = HTTPParser().parse(client.getInputStream())
            Router().route(request, client)
        } catch (e: Exception) {
            error(e)
        } finally {
            client.close()
        }
    }

    fun error(e: Exception) {
        e.printStackTrace()
        try {
            val request = HTTPParser().parse(client.getInputStream())
            Router().error(client, e)
        } catch (error: Exception) {
            error.printStackTrace()
        } finally {
            client.close()
        }
    }
}