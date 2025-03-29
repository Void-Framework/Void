package io.void.http.parser

import io.void.api.method.Method
import io.void.dto.http.RequestDTO
import io.void.router.Router
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.util.*


class HTTPParser {

    private val headers: MutableMap<String, String> = mutableMapOf()
    private lateinit var method: Method
    private lateinit var path: String

    fun parse(inputStream: InputStream, client: Socket): RequestDTO {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val line = reader.readLine()?.split(" ") ?: throw IllegalStateException("Empty request received")
        try {
            if (line.size < 2) throw IllegalArgumentException("Invalid request line")
            method = Method.valueOf(line[0].uppercase(Locale.getDefault()))
        } catch (e: Exception) {
            Router().error(client, e)
            return RequestDTO(
                method = Method.GET,
                target = "/",
                headers = headers,
                body = ""
            ) // Provide a default request
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

        val requestDTO = RequestDTO(
            method = method,
            target = path,
            headers = headers,
            body = body.toString()
        )

        return requestDTO
    }
}