package io.void.clienthandler

import io.void.dto.http.RequestDTO
import io.void.dto.http.writeHTTP
import io.void.router.Router
import io.void.router.toResult
import io.void.server.Server
import java.net.Socket

/**
 * Handles a single client connection end-to-end.
 *
 * Reads the HTTP request from [client], delegates routing to [router],
 * and writes the produced response back over the socket using the server's
 * configured HTTP version. On any exception, middleware and the router's
 * error page are invoked via [error].
 */
class ClientHandler(
    val client: Socket,
    val server: Server,
    val router: Router,
) {
    /**
     * Starts processing for this connection: parse request, route it, and write the response.
     * Ensures the client socket is closed when finished.
     */
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

    /**
     * Handles an exception from request processing. Gives BEFORE middleware a chance to
     * produce a custom [ResponseDTO], otherwise delegates to the router's error handler.
     */
    fun error(e: Exception) {
        val response = router.middlewareProcessBefore(e.toResult())
        if (response != null) {
            client.getOutputStream().writeHTTP(
                response = response,
                version = server.httpVersion,
            )
            return
        }
        try {
            this.router.error(this, e)
        } catch (error: Exception) {
            error.printStackTrace()
        } finally {
            client.close()
        }
    }
}
