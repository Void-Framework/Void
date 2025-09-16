package io.void.dto.http

import java.io.OutputStream
import java.io.PrintWriter
import kotlin.reflect.full.memberProperties

typealias JSON = MutableMap<String, Any?>
typealias Headers = MutableMap<String, String>

data class ResponseDTO(var status: Int, var statusText: String, var headers: Headers, var body: String) {

    companion object {
        fun build(response: ResponseDTO, outputStream: OutputStream, version: Number = 1.1)  {
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