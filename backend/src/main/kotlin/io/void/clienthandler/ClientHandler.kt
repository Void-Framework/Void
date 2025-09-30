package io.void.clienthandler

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.dto.http.writeHTTP
import io.void.router.Router
import io.void.router.toResult
import io.void.router.util.MiddlewareTime
import io.void.server.Server
import java.net.Socket

class ClientHandler(
    val client: Socket,
    val server: Server,
    val router: Router,
) {
    fun start() {
        try {
            val request =
                RequestDTO.parse(
                    inputStream = client.getInputStream(),
                    clientHandler = this,
                )
            this.router.route(
                requestDTO = request,
                clientHandler = this,
            )
        } catch (e: Exception) {
            error(e)
        } finally {
            client.close()
        }
    }

    fun error(
        e: Exception,
        time: MiddlewareTime,
    ) {
        val response = router.middlewareProcess(e.toResult(), time)
        if (response != null) {
            client.getOutputStream().writeHTTP(
                response = response,
                version = server.httpVersion
            )
            return
        }
        e.printStackTrace()
        try {
            this.router.error(this, e)
        } catch (error: Exception) {
            error.printStackTrace()
        } finally {
            client.close()
        }
    }
}
