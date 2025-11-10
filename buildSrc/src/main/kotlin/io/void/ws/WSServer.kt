package io.void.ws

import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

object TinyWebSocketServer {
    private val connections = CopyOnWriteArrayList<Socket>()
    private var started = false

    fun start(port: Int = 35729) {
        if (started) return
        started = true

        thread(name = "TinyWS") {
            val server = ServerSocket(port)
            println("⚡ Tiny WS server listening on ws://localhost:$port/reload")

            while (true) {
                val socket = server.accept()
                thread {
                    try {
                        handleHandshake(socket)
                        connections += socket
                        println("💬 Browser connected (${connections.size})")
                        listenLoop(socket)
                    } catch (e: Exception) {
                        socket.close()
                    } finally {
                        connections -= socket
                    }
                }
            }
        }
    }

    private fun handleHandshake(socket: Socket) {
        val input = socket.getInputStream().bufferedReader()
        val output = socket.getOutputStream()
        val request = buildString {
            var line: String?
            while (input.readLine().also { line = it } != null && line!!.isNotEmpty()) {
                appendLine(line)
            }
        }

        val key = Regex("Sec-WebSocket-Key: (.*)")
            .find(request)?.groupValues?.get(1)?.trim()
            ?: throw IllegalStateException("Missing WS key")

        val accept = Base64.getEncoder().encodeToString(
            MessageDigest.getInstance("SHA-1")
                .digest("${key}258EAFA5-E914-47DA-95CA-C5AB0DC85B11".toByteArray())
        )

        output.write(
            """
            HTTP/1.1 101 Switching Protocols
            Upgrade: websocket
            Connection: Upgrade
            Sec-WebSocket-Accept: $accept

            """.trimIndent().toByteArray()
        )
        output.flush()
    }

    private fun listenLoop(socket: Socket) {
        val input = socket.getInputStream()
        while (true) {
            if (input.read() == -1) break // client closed
        }
    }

    fun broadcast(message: String) {
        val data = message.encodeToByteArray()
        val frame = buildFrame(data)
        connections.removeIf {
            try {
                it.getOutputStream().write(frame)
                false
            } catch (_: Exception) {
                true
            }
        }
    }

    private fun buildFrame(data: ByteArray): ByteArray {
        val payloadLen = data.size
        val header = mutableListOf<Byte>()
        header += 0x81.toByte() // FIN + text frame
        header += when {
            payloadLen <= 125 -> payloadLen.toByte()
            payloadLen <= 65535 -> 126.toByte()
                .also { header.addAll(listOf((payloadLen shr 8).toByte(), payloadLen.toByte())) }

            else -> throw IllegalArgumentException("Payload too big")
        }
        return header.toByteArray() + data
    }
}
