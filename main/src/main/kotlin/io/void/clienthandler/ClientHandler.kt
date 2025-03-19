package io.void.clienthandler

import io.void.http.parser.HTTPParser
import io.void.router.Router
import io.void.server.Server
import io.void.ws.WSClient
import java.net.InetAddress
import java.net.Socket
import java.net.URI

class ClientHandler(val client: Socket, server: Server) {

    private lateinit var router: Router
    private val parser = HTTPParser()
    var wsClient: WSClient

    init {

        val localAddress = InetAddress.getLocalHost().hostAddress
        wsClient = WSClient(URI.create("ws://${localAddress}:${server.port + 1}/"))
        wsClient.connect()
    }

    fun setRouter(router: Router): ClientHandler {
        this.router = router
        return this
    }

    fun start(server: Server) {
        try {
            val request = parser.parse(
                inputStream = client.getInputStream(),
                client = client
            )
            this.router.route(
                requestDTO = request,
                clientHandler = this
            )
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