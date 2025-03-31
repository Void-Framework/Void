package io.void.js.keywords

import io.void.js.JavaScript

data class Return<T>(val value: T?): Keyword {

    override fun render(): String {
        return "return $value;"
    }
}

inline fun <reified T> JavaScript.Return(_value: T?): Return<T> {
    val Return = Return(value = _value)
    return Return
}