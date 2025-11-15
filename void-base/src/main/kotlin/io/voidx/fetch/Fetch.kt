package io.voidx.fetch

import io.voidx.dto.http.RequestDTO
import io.voidx.dto.http.ResponseDTO
import io.voidx.dto.http.buildResponse
import io.voidx.dto.http.toHttpRequest
import java.net.http.HttpClient
import java.net.http.HttpResponse

internal val client = HttpClient.newHttpClient()

/**
 * Executes an HTTP request using the shared [HttpClient] and returns a [Result] of [ResponseDTO].
 *
 * Behavior:
 * - The provided [requestDTO] is converted to a Java `HttpRequest` via [RequestDTO.toHttpRequest] using the given [url].
 * - The call is executed synchronously and wrapped with [runCatching], so any thrown exception
 *   (e.g., connection failure, invalid URL) is captured as a failed `Result`.
 * - Response headers are normalized to Title-Case keys (e.g., `Content-Type`, `Cache-Control`).
 *   Multiple header values are joined with ",".
 *
 * Parameters:
 * - `url`: Base URL such as `"http://localhost:8080"`. The request path from [RequestDTO.target]
 *          is appended to this base URL.
 * - `requestDTO`: High-level request description (method, headers, body, path).
 *
 * Returns:
 * - `Result<ResponseDTO>` where `isSuccess` indicates a successful HTTP exchange and `isFailure`
 *   captures exceptions thrown during request creation or execution.
 *
 * Usage example:
 * ```kotlin
 * val req = buildRequest {
 *     method = Method.GET
 *     target = "/"
 * }
 * val result = fetch("http://localhost:8080", req)
 * val response = result.getOrElse { error ->
 *     // handle error
 *     buildResponse { status = 599; statusText = "Network Error"; body = error.message ?: "" }
 * }
 * println(response.status)
 * ```
 */
fun fetch(
    url: String,
    requestDTO: RequestDTO,
): Result<ResponseDTO> =
    runCatching {
        val request = requestDTO.toHttpRequest(url)
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        buildResponse {
            status = response.statusCode()
            body = response.body()
            headers =
                response
                    .headers()
                    .map()
                    .mapKeys { (k, _) ->
                        k
                            .split('-')
                            .joinToString("-") { part ->
                                val lower = part.lowercase()
                                lower.replaceFirstChar { ch -> if (ch.isLowerCase()) ch.titlecase() else ch.toString() }
                            }
                    }.mapValues { (_, v) -> v.joinToString(",") }
                    .toMutableMap()
        }
    }
