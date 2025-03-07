package main.java.main.HTTP.Builder

import main.java.main.DTO.ResponseDTO
import java.io.OutputStream
import java.io.PrintWriter



class HTTPBuilder {

    fun build(response: ResponseDTO, outputStream: OutputStream)  {
        val writer = PrintWriter(outputStream, true)
        writer.println(("HTTP/1.1 " + response.status) + " " + response.statusText)
        for ((key, value) in response.headers.entries) {
            writer.println("$key: $value")
        }
        writer.println()
        writer.println(response.body)
    }
}