package io.void.clienthandler

import io.void.http.builder.HTTPBuilder
import io.void.http.parser.HTTPParser
import io.void.router.Router
import java.net.Socket

class ClientHandler(private val client: Socket) {

    private lateinit var router: Router
    private val parser = HTTPParser()
    private val builder = HTTPBuilder()

    fun setRouter(router: Router): ClientHandler {
        this.router = router
        return this
    }

    fun start() {
        try {
            val request = parser.parse(
                inputStream = client.getInputStream(),
                client = client
            )
            this.router.route(request, client)
        } catch (e: Exception) {
            error(e)
        } finally {
            client.close()
        }
    }

    fun error(e: Exception) {
        val response = router.middlewareHandleError(e)
        if (response != null) {
            builder.build(
                response = response,
                outputStream = client.getOutputStream()
            )
            return
        }
        e.printStackTrace()
        try {
            this.router.error(client, e)
        } catch (error: Exception) {
            error.printStackTrace()
        } finally {
            client.close()
        }
    }
}