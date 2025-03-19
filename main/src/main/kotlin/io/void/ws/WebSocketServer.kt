package main.java.main.WebSocket

import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress

class WebSocketServer: WebSocketServer(InetSocketAddress(8081)) {

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
        println("Message: $p1")
    }

    override fun onClose(p0: WebSocket?, p1: Int, p2: String?, p3: Boolean) {
        connections.forEach { it.close() }
        stop()
    }

}