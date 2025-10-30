package io.void.dto.http

import io.void.api.method.Method
import io.void.clienthandler.ClientHandler
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.http.HttpRequest
import java.util.*

data class RequestDTO(
    val method: Method,
    val target: String,
    val headers: Map<String, String>,
    val body: String,
) {
    companion object {
        internal fun parse(
            inputStream: InputStream,
            clientHandler: ClientHandler,
        ): RequestDTO {
            val headers: MutableMap<String, String> = mutableMapOf()
            val method: Method
            val path: String
            val reader = BufferedReader(InputStreamReader(inputStream))
            val line = reader.readLine()?.split(" ") ?: throw IllegalStateException("Empty request received")
            try {
                if (line.size < 2) throw IllegalArgumentException("Invalid request line")
                method = Method.valueOf(line[0].uppercase(Locale.getDefault()))
            } catch (e: Exception) {
                clientHandler.router.error(clientHandler, e)
                return buildRequest {
                    this.method = Method.GET
                    target = "/"
                    this.headers.putAll(headers)
                    body = ""
                } // Provide a default request
            }
            path = line[1]

            var headerLine: String?
            while ((reader.readLine().also { headerLine = it }) != null && headerLine!!.isNotEmpty()) {
                val header = headerLine.split(": ", limit = 2)
                if (header.size == 2) headers[header[0]] = header[1]
            }

            val body = StringBuilder()
            val contentLength = headers["Content-Length"]?.toIntOrNull()
            if (contentLength != null && contentLength > 0) {
                val charArray = CharArray(contentLength)
                reader.read(charArray, 0, contentLength)
                body.append(charArray)
            }

            val requestDTO: RequestDTO =
                buildRequest {
                    this.method = method
                    this.target = path
                    this.headers.putAll(headers)
                    this.body = body.toString()
                }

            return requestDTO
        }
    }

    operator fun get(headerName: String): String? = headers[headerName]
}

class RequestBuilder {
    var method: Method = Method.GET
    var target: String = "/"
    val headers: MutableMap<String, String> = mutableMapOf()
    var body: String = ""

    fun build(): RequestDTO = RequestDTO(method, target, headers.toMap(), body)
}

fun buildRequest(builder: RequestBuilder.() -> Unit): RequestDTO {
    val build = RequestBuilder()
    build.builder()
    return build.build()
}

fun RequestBuilder.headers(block: MutableMap<String, String>.() -> Unit) {
    headers.block()
}

fun RequestDTO.toHttpRequest(url: String): HttpRequest {
    val builder = HttpRequest.newBuilder()
        .uri(URI.create(url + target))
        .method(method.name,
            if (body.isNotEmpty()) HttpRequest.BodyPublishers.ofString(body)
            else HttpRequest.BodyPublishers.noBody()
        )

    headers.forEach { (key, value) -> builder.header(key, value) }

    return builder.build()
}
