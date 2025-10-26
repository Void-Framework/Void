package io.void.middleware

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO

interface Relay {
    val priority: Int
}

interface RelayBefore : Relay {
    fun processBefore(requestDTO: Result<RequestDTO>): ResponseDTO?
}

interface RelayAfter : Relay {
    fun processAfter(response: Result<ResponseDTO>)
}

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

class RelayBeforeBuilder {
    var before: (Result<RequestDTO>) -> ResponseDTO? = { null }
}

class RelayAfterBuilder {
    var after: (Result<ResponseDTO>) -> Unit = { }
}
