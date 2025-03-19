package io.void.clienthandler

import io.void.http.parser.HTTPParser
import io.void.router.Router
import io.void.server.Server
import io.void.ws.WSClient
import io.void.ws.WSServer
import java.net.Socket
import java.net.URI

class ClientHandler(private val client: Socket, wsServer: WSServer) {

    private lateinit var router: Router
    private val parser = HTTPParser()
    var wsClient: WSClient

    init {
        wsClient = WSClient(URI.create("ws://${wsServer.address}/"))
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