package io.void.ws

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.UUID

class WSClient(uri: URI): WebSocketClient(uri) {

    private var uuid: UUID

    init {
        uuid = UUID.randomUUID()
    }

    override fun onOpen(p0: ServerHandshake?) {
        println("Connected to server")
        send("REGISTER:$uuid")
    }

    override fun onClose(p0: Int, p1: String?, p2: Boolean) {
        println("Connection closed")
    }

    override fun onMessage(p0: String?) {
        println("Received message: $p0")
    }

    override fun onError(p0: Exception?) {
        p0!!.printStackTrace()
    }
}