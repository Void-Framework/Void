package main.java.main.ClientHandler

import main.java.main.HTTP.Parser.HTTPParser
import main.router.Router
import java.net.Socket

class ClientHandler(private val client: Socket) {

    private lateinit var router: Router

    fun setRouter(router: Router): ClientHandler {
        this.router = router
        return this
    }

    fun start() {
        try {
            val request = HTTPParser().parse(client.getInputStream())
            this.router.route(request, client)
        } catch (e: Exception) {
            error(e)
        } finally {
            client.close()
        }
    }

    fun error(e: Exception) {
        e.printStackTrace()
        try {
            this.router.error(client, e)
        } catch (error: Exception) {
            error.printStackTrace()
        } finally {
            client.close()
        }
    }
}