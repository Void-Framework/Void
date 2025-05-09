package io.void.js.keywords

import io.void.js.JavaScript

data class Return<T>(val value: T?): Keyword {

    override var jsReturn: String = "return $value;"

    override fun render(): String {
        return jsReturn
    }
    fun emptyReturn(): Return<T> {
        jsReturn = "return;"
        return this
    }
}

inline fun <reified T> JavaScript.Return(_value: T?): Return<T> {
    val Return = Return(value = _value)
    children.add(Return)
    return Return
}
fun JavaScript.Return(): Return<Nothing> {
    val Return = Return(value = null)
    children.add(Return)
    Return.emptyReturn()
    return Return
}