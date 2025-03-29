package io.void.http.builder

import io.void.dto.ResponseDTO
import java.io.OutputStream
import java.io.PrintWriter

class HTTPBuilder {

    companion object {
        var version: Number = 3
    }

    fun build(response: ResponseDTO, outputStream: OutputStream)  {
        val writer = PrintWriter(outputStream, true)
        writer.println("HTTP/$version ${response.status} ${response.statusText}")

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