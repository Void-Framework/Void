package io.voidx

import io.voidx.dto.RequestDTO
import io.voidx.dto.writeHTTP
import io.voidx.router.Router
import io.voidx.util.toResult
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
     * - Write the [io.voidx.dto.ResponseDTO] back using [Server.httpVersion].
     * - On any exception, delegate to [error].
     */
    internal fun start() {
        try {
            val request =
                RequestDTO.Companion.parse(
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
     * - Invokes global BEFORE middleware; if any returns a [io.voidx.dto.ResponseDTO], that is sent immediately.
     * - Otherwise, delegates to [Router.error] which renders the configured [io.voidx.page.ExceptionPage].
     * - Closes the client socket in all cases.
     *
     * @param e The exception thrown during request handling.
     */
    internal fun error(e: Exception) {
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
