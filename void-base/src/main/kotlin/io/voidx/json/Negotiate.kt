package io.voidx.json

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO

/**
 * Handles content negotiation for a given [RequestDTO].
 *
 * This class provides a chaining DSL for content negotiation. Callers typically chain multiple
 * [whenType] (or specialized helpers like [json]) blocks and use the infix [or] as a required fallback.
 * If no branch matches, the fallback must return an HTTP 415 Unsupported Media Type response.
 *
 * @property request The request to perform negotiation on.
 */
class Negotiator(
    val request: RequestDTO,
) {
    /**
     * Defines a type used for content negotiation based on a content type string.
     */
    abstract class NegotiateType {
        /**
         * The primary MIME type associated with this negotiate type.
         */
        abstract val contentType: String

        /**
         * Determines whether the given request satisfies this negotiate type's criteria.
         *
         * @param request The request to evaluate.
         * @return `true` if the request matches this negotiate type's criteria, `false` otherwise.
         */
        abstract fun matches(request: RequestDTO): Boolean
    }

    /**
     * Executes the given block and returns its result when the provided [NegotiateType] matches the negotiator's request.
     *
     * This method is intended to be used as part of a content negotiation chain.
     *
     * @param type The NegotiateType used to test the request.
     * @param block Lambda to execute when the type matches; its return value is propagated.
     * @return The block's result if the type matches the request, `null` otherwise.
     */
    fun <T> whenType(
        type: NegotiateType,
        block: () -> T,
    ): T? = if (type.matches(request)) block() else null

    /**
     * Provide a fallback [ResponseDTO] by invoking the given Negotiator-scoped block when the receiver is null.
     *
     * This is intended as the final fallback in a negotiation chain. If no preceding `whenType` block matches,
     * this block is executed. It is recommended to return an HTTP 415 Unsupported Media Type response here.
     *
     * Example usage:
     * ```kotlin
     * negotiator.json { ResponseDTO.ok(...) } or { unsupportedMediaType() }
     * ```
     * It is recommended to provide a convenience helper method (e.g., `unsupportedMediaType()`) to produce
     * a 415 [ResponseDTO] when no negotiator matches.
     *
     * @param block Fallback factory invoked on a Negotiator when the receiver is null; may return null.
     * @return The original response if not null, otherwise the response produced by `block`.
     */
    infix fun ResponseDTO?.or(block: Negotiator.() -> ResponseDTO?): ResponseDTO? = this ?: block()


    /**
     * Provide a fallback ResponseDTO by invoking the given block when the receiver is null.
     *
     * @param block Fallback factory when the receiver is null.
     * @return The original response if not null, otherwise the response produced by `block`.
     */
    infix fun ResponseDTO?.orElse(block: (RequestDTO) -> ResponseDTO): ResponseDTO = this ?: block(request)
}

/**
 * A [Negotiator.NegotiateType] implementation for "application/json".
 */
object JsonType : Negotiator.NegotiateType() {
    override val contentType: String = "application/json"

    /**
     * Checks whether the request's "Content-Type" header starts with this negotiate type's content type.
     *
     * @param request The request to inspect.
     * @return `true` if the request's "Content-Type" header starts with `contentType`, `false` otherwise.
     */
    override fun matches(request: RequestDTO): Boolean = request["Content-Type"]?.startsWith(contentType) == true
}

/**
 * Executes [block] when the request's Content-Type header starts with "application/json".
 *
 * @param block Produces the response to return when the request matches the JSON content type.
 * @return The `ResponseDTO` produced by [block] if the request Content-Type starts with "application/json", `null` otherwise.
 */
fun Negotiator.json(block: () -> ResponseDTO): ResponseDTO? = whenType(JsonType, block)
