package io.void.dto.http

import io.void.api.method.Method
import java.io.OutputStream
import java.io.PrintWriter
import kotlin.reflect.full.memberProperties

typealias Headers = MutableMap<String, String>

data class ResponseDTO(
    val status: Int,
    val statusText: String,
    val headers: Headers,
    val body: String,
) {
    companion object {
        internal fun build(
            response: ResponseDTO,
            outputStream: OutputStream,
            version: Number = 1.1,
        ) {
            val writer = PrintWriter(outputStream, true)
            writer.println("HTTP/$version ${response.status} ${response.statusText}")

            val responseBody = response.body
            if (!response.headers.containsKey("Content-Length")) {
                response.headers["Content-Length"] = responseBody.toByteArray().size.toString()
            }

            for ((key, value) in response.headers.entries) {
                writer.println("$key: $value")
            }
            writer.println()
            writer.println(response.body)

            writer.flush()
        }
    }
}

class ResponseBuilder {
    var status: Int = 200
    var statusText: String = "All is well!"
    val headers: MutableMap<String, String> = mutableMapOf()
    var body: String = ""

    fun build(): ResponseDTO = ResponseDTO(status, statusText, headers, body)
}

fun buildResponse(builder: ResponseBuilder.() -> Unit): ResponseDTO {
    val build = ResponseBuilder()
    build.builder()
    return build.build()
}

fun ResponseBuilder.headers(block: Headers.() -> Unit) {
    headers.block()
}
