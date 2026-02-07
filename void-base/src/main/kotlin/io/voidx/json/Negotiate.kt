package io.voidx.json

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO

class Negotiator(
    val request: RequestDTO
) {

    abstract class NegotiateType {
        abstract val contentType: String
        abstract fun matches(request: RequestDTO): Boolean
    }

    fun <T> whenType(
        type: NegotiateType,
        block: () -> T
    ): T? = if (type.matches(request)) block() else null
}

object JsonType : Negotiator.NegotiateType() {
    override val contentType: String = "application/json"

    override fun matches(request: RequestDTO): Boolean = request["Content-Type"]?.startsWith(contentType) == true

}

fun Negotiator.json(block: () -> ResponseDTO): ResponseDTO? =
    whenType(JsonType, block)
