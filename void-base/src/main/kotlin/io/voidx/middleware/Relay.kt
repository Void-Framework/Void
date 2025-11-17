package io.voidx.middleware

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO

/**
 * Marker interface for a middleware unit ("relay") that can participate in request/response
 * processing. A higher [priority] runs earlier for BEFORE relays and earlier for AFTER relays.
 */
interface Relay {
    val priority: Int
}

/**
 * Middleware that runs before a page/route handler. If it returns a non-null [ResponseDTO],
 * routing is short-circuited and that response is sent to the client.
 */
interface RelayBefore : Relay {
    fun processBefore(requestDTO: Result<RequestDTO>): ResponseDTO?
}

/** Middleware that runs after the handler with the produced [ResponseDTO]. */
interface RelayAfter : Relay {
    fun processAfter(response: Result<ResponseDTO>)
}

/**
 * DSL factory for creating a [RelayBefore] with an optional [priority].
 *
 * Example:
 * val auth = relayBefore(priority = 10) { req -> /* validate */ null }
 */
fun relayBefore(
    priority: Int = 0,
    block: (Result<RequestDTO>) -> ResponseDTO?,
): RelayBefore =
    object : RelayBefore {
        override val priority = priority
        private val builder =
            RelayBeforeBuilder().apply {
                before = block
            }

        override fun processBefore(requestDTO: Result<RequestDTO>) = builder.before(requestDTO)
    }

/** DSL factory for creating a [RelayAfter] with an optional [priority]. */
fun relayAfter(
    priority: Int = 0,
    block: (Result<ResponseDTO>) -> Unit,
): RelayAfter =
    object : RelayAfter {
        override val priority = priority
        private val builder =
            RelayAfterBuilder().apply {
                after = block
            }

        override fun processAfter(response: Result<ResponseDTO>) {
            builder.after(response)
        }
    }

/** Backing builder used by [relayBefore]. */
class RelayBeforeBuilder {
    var before: (Result<RequestDTO>) -> ResponseDTO? = { null }
}

/** Backing builder used by [relayAfter]. */
class RelayAfterBuilder {
    var after: (Result<ResponseDTO>) -> Unit = { }
}
