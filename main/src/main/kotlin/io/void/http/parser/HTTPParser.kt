package io.void.http.parser

import io.void.api.method.Method
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.http.builder.HTTPBuilder
import io.void.router.Router
import io.void.server.Server
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.util.*


class HTTPParser {

    private val headers: MutableMap<String, String> = mutableMapOf()
    private lateinit var method: Method
    private lateinit var path: String

    fun parse(inputStream: InputStream, client: Socket, server: Server): RequestDTO {
        if (checkForClientAndServerHTTPS(
            client = client,
            server = server
        )) return RequestDTO(Method.GET, "/", headers, "") // Provide a default request
        val reader = BufferedReader(InputStreamReader(inputStream))
        val line = reader.readLine()?.split(" ") ?: throw IllegalStateException("Empty request received")
        try {
            if (line.size < 2) throw IllegalArgumentException("Invalid request line")
            method = Method.valueOf(line[0].uppercase(Locale.getDefault()))
        } catch (e: Exception) {
            Router().error(client, e)
            return RequestDTO(Method.GET, "/", headers, "") // Provide a default request
        }
        path = line[1]

        var headerLine: String?
        while ((reader.readLine().also { headerLine = it }) != null && headerLine!!.isNotEmpty()) {
            val header = headerLine!!.split(": ", limit = 2)
            if (header.size == 2) headers[header[0]] = header[1]
        }

        val body = StringBuilder()
        val contentLength = headers["Content-Length"]?.toIntOrNull()
        if (contentLength != null && contentLength > 0) {
            val charArray = CharArray(contentLength)
            reader.read(charArray, 0, contentLength)
            body.append(charArray)
        }

        val requestDTO = RequestDTO(method, path, headers, body.toString())

        return requestDTO
    }

    private fun checkForClientAndServerHTTPS(client: Socket, server: Server): Boolean {
        val inputStream = client.getInputStream()
        val sssStream = inputStream.mark(5)
        val firstBytes = ByteArray(5)

        inputStream.read(firstBytes)
        inputStream.reset()

        val isHTTPS = firstBytes[0] == 0x16.toByte()

        if (isHTTPS && server.isHTTP) {
            HTTPBuilder().build(
                ResponseDTO(
                    status = 301,
                    statusText = "Moved Permamently",
                    headers = mutableMapOf(),
                    body = "Location: http://${client.inetAddress.hostAddress}:${server.port}"
                ),
                client.getOutputStream()
            )
            return true
        } else {
            return false
        }
    }
}