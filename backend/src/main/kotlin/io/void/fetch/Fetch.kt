package io.void.fetch

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.dto.http.toHttpRequest
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.http.HttpClient
import java.net.http.HttpResponse

/**
 * A minimal Promise-like wrapper around a Kotlin [Deferred] to allow a chainable API
 * similar to JavaScript promises for simple async workflows.
 *
 * Notes and caveats:
 * - Errors are captured only when using [then]. If the original asynchronous block fails
 *   and you never call [then], the error will propagate on [finally]. To recover using [catch],
 *   you must call [then] first so the exception can be observed and stored.
 */
 data class Promise<T : Any>(
    private val deferred: Deferred<T>
) {

    private var exception: Exception? = null

    companion object {
        /**
         * Launches an asynchronous computation on [Dispatchers.IO] and wraps it in a [Promise].
         */
        @OptIn(DelicateCoroutinesApi::class)
        fun <T : Any> async(block: suspend () -> T): Promise<T> {
            val deferred = GlobalScope.async(Dispatchers.IO) { block() }
            return Promise(deferred)
        }

        /** Shared JVM HTTP client used by [fetch]. */
        internal val client = HttpClient.newHttpClient()
    }

    /**
     * Chains the next step in the computation. If the current deferred completes normally,
     * the [next] function is invoked and its result is wrapped into a new [Promise].
     * If awaiting the current value throws, the exception is captured and the same [Promise]
     * instance is returned, allowing a subsequent [catch] to handle it.
     */
    suspend fun then(next: suspend (T) -> T): Promise<T> {
        return try {
            val result = next(deferred.await())
            async { result }
        } catch (e: Exception) {
            exception = e
            this
        }
    }

    /**
     * Handles an exception captured by a previous [then] stage. If an exception exists,
     * the [handler] is invoked and its return value is wrapped into a new successful [Promise].
     * If there is no exception, the same [Promise] instance is returned untouched.
     */
    suspend fun catch(handler: suspend (Exception?) -> T): Promise<T> {
        return if (exception != null) {
            val result = handler(exception)
            async { result }
        } else {
            this
        }
    }

    /**
     * Terminal operation that awaits the underlying value and passes it to [unwrap].
     * If the underlying computation fails and the error was not previously handled via [then]/[catch],
     * the exception is rethrown to the caller of [finally].
     */
    suspend fun finally(unwrap: suspend (T) -> Unit) {
        try {
            val value = deferred.await()
            unwrap(value)
        } catch (e: Exception) {
            unwrap(throw e)
        }
    }
}

/**
 * Executes an HTTP request using the shared [HttpClient] and returns a [Promise] that resolves
 * to a lightweight [ResponseDTO]. The provided [requestDTO] is converted to a Java HTTP request
 * via `toHttpRequest(url)` and executed synchronously inside an IO coroutine.
 *
 * @param url Base URL, e.g. "http://localhost:8080". The request path from [RequestDTO.target]
 *            will be appended to this base URL.
 * @param requestDTO High-level request description (method, headers, body, path).
 * @return [Promise] resolving to [ResponseDTO] that contains status, headers, and body as String.
 */
fun fetch(url: String, requestDTO: RequestDTO) : Promise<ResponseDTO> {
    return Promise.async {
        val request = requestDTO.toHttpRequest(url)
        val response = Promise.client.send(request, HttpResponse.BodyHandlers.ofString())

        buildResponse {
            status = response.statusCode()
            body = response.body()
            headers = response.headers().map()
                .mapKeys { (k, _) ->
                    k.split('-')
                        .joinToString("-") { part ->
                            val lower = part.lowercase()
                            lower.replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
                        }
                }
                .mapValues { (_, v) -> v.joinToString(",") }
                .toMutableMap()
        }
    }
}