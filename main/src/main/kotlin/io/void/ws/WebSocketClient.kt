package main.WebSocket

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class WebSocketClient: WebSocketClient(URI("ws://localhost:8081/")) {

    override fun onOpen(p0: ServerHandshake?) {
        println("Connected to server")
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