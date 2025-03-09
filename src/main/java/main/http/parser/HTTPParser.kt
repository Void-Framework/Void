package main.java.main.HTTP.Parser

import main.api.method.Method
import main.java.main.DTO.RequestDTO
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


class HTTPParser {

    private val headers: MutableMap<String, String> = mutableMapOf()
    private lateinit var method: Method
    private lateinit var path: String

    fun parse(inputSteam: InputStream): RequestDTO {
        val reader = BufferedReader(InputStreamReader(inputSteam))
        var line = reader.readLine()?.split(" ") ?: throw IllegalStateException("Empty request received")
        if (line.size < 2) throw IllegalArgumentException("Invalid request line")
        method = Method.valueOf(line[0].uppercase(Locale.getDefault()))
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
}