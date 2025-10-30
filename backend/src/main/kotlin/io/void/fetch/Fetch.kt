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

data class Promise<T : Any>(
    private val deferred: Deferred<T>
) {

    private var exception: Exception? = null

    companion object {
        @OptIn(DelicateCoroutinesApi::class)
        fun <T : Any> async(block: suspend () -> T): Promise<T> {
            val deferred = GlobalScope.async(Dispatchers.IO) { block() }
            return Promise(deferred)
        }

        internal val client = HttpClient.newHttpClient()
    }

    suspend fun then(next: suspend (T) -> T): Promise<T> {
        return try {
            val result = next(deferred.await())
            async { result }
        } catch (e: Exception) {
            exception = e
            this
        }
    }

    suspend fun catch(handler: suspend (Exception?) -> T): Promise<T> {
        return if (exception != null) {
            val result = handler(exception)
            async { result }
        } else {
            this
        }
    }

    suspend fun finally(unwrap: suspend (T) -> Unit) {
        try {
            val value = deferred.await()
            unwrap(value)
        } catch (e: Exception) {
            unwrap(throw e)
        }
    }
}

fun fetch(url: String, requestDTO: RequestDTO) : Promise<ResponseDTO> {
    return Promise.async {
        val request = requestDTO.toHttpRequest(url)
        val response = Promise.client.send(request, HttpResponse.BodyHandlers.ofString())

        buildResponse {
            status = response.statusCode()
            body = response.body()
            headers = response.headers().map()
                .mapValues { (_, v) -> v.joinToString(",") }
                .toMutableMap()
        }
    }
}