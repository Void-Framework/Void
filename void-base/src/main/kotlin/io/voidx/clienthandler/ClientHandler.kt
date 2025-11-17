package io.voidx.clienthandler

import io.voidx.dto.http.RequestDTO
import io.voidx.dto.http.writeHTTP
import io.voidx.router.Router
import io.voidx.router.toResult
import io.voidx.server.Server
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
     *
     * Flow:
     * - Parse a [RequestDTO] from [client].
     * - Delegate to [Router.route] which runs global and per-page middleware and renders content.
     * - Write the [io.voidx.dto.http.ResponseDTO] back using [Server.httpVersion].
     * - On any exception, delegate to [error].
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
     * Handles an exception from request processing.
     *
     * Behavior:
     * - Invokes global BEFORE middleware; if any returns a [ResponseDTO], that is sent immediately.
     * - Otherwise, delegates to [Router.error] which renders the configured [io.voidx.page.ExceptionPage].
     * - Closes the client socket in all cases.
     *
     * @param e The exception thrown during request handling.
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
