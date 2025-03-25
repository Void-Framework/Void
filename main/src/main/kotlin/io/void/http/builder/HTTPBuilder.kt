package io.void.http.builder

import io.void.dto.ResponseDTO
import java.io.OutputStream
import java.io.PrintWriter



class HTTPBuilder {

    fun build(response: ResponseDTO, outputStream: OutputStream)  {
        val writer = PrintWriter(outputStream, true)
        writer.println("HTTP/3 ${response.status} ${response.statusText}")

        val responseBody = response.body ?: ""
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