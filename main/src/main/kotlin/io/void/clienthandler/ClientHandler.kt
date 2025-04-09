package io.void.clienthandler

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.router.Router
import java.net.Socket

class ClientHandler(private val client: Socket) {

    private lateinit var router: Router

    fun setRouter(router: Router): ClientHandler {
        this.router = router
        return this
    }

    fun start() {
        try {
            val request = RequestDTO.parse(
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
            ResponseDTO.build(
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