package io.void.ws

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.*

class WSServer(port: Int): WebSocketServer(InetSocketAddress(port)) {

    private val clients = mutableMapOf<UUID, WSClient>()

    private fun registerClient(uuid: UUID, client: WSClient) {
        clients[uuid] = client
    }

    fun getClient(uuid: UUID): WSClient? {
        return clients[uuid]
    }

    override fun onStart() {
        println("Started")
    }

    override fun onOpen(p0: WebSocket?, p1: ClientHandshake?) {
        println("Opened")
    }

    override fun onError(p0: WebSocket?, p1: Exception?) {
        throw Exception(p1!!.message)
    }

    override fun onMessage(p0: WebSocket?, p1: String?) {
        p0?.send("Server received: $p1")
        if (p1 != null && p1.startsWith("REGISTER:")) {
            val uuid = UUID.fromString(p1.substringAfter("REGISTER:"))
            registerClient(
                uuid = uuid,
                client = p0 as WSClient
            )
        }
        println("Message: $p1")
    }

    override fun onClose(p0: WebSocket?, p1: Int, p2: String?, p3: Boolean) {
        connections.forEach { it.close() }
        stop()
    }

}