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
        var line = reader.readLine() ?: throw IllegalStateException("Empty request received")
        val requestLine = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        method = Method.valueOf(requestLine[0].uppercase(Locale.getDefault()))
        path = requestLine[1]

        while ((reader.readLine().also { line = it }).isNotEmpty()) {
            val header = line.split(": ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            headers[header[0]] = header[1]
        }

        val body = StringBuilder()
        while (reader.ready()) {
            body.append(reader.read().toChar())
        }

        val requestDTO = RequestDTO(method, path, headers, body.toString())

        return requestDTO
    }
}