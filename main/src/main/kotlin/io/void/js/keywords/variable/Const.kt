package io.void.js.keywords.variable

import io.void.js.JavaScript
import io.void.js.keywords.Keyword
import io.void.js.keywords.RawJs

data class Const<T>(
    override val value: T?,
    override val name: String,
    val parent: JavaScript
): Variable<T> {

    override var jsReturn: String = "const $name = ${if (value is Keyword) {
        value.render()
    } else {
        "$value"
    }
    }"
    operator fun plus(other: T) {
        parent.children.add(RawJs("$name + $other"))
    }
    operator fun plus(other: Variable<T>) {
        parent.children.add(RawJs("$name + $other"))
    }
    operator fun minus(other: T) {
        parent.children.add(RawJs("$name - $other"))
    }
    operator fun minus(other: Variable<T>) {
        parent.children.add(RawJs("$name - $other"))
    }
    operator fun times(other: T) {
        parent.children.add(RawJs("$name * $other"))
    }
    operator fun times(other: Variable<T>) {
        parent.children.add(RawJs("$name * $other"))
    }
    operator fun div(other: T) {
        parent.children.add(RawJs("$name / $other"))
    }
    operator fun div(other: Variable<T>) {
        parent.children.add(RawJs("$name / $other"))
    }
    operator fun rem(other: T) {
        parent.children.add(RawJs("$name % $other"))
    }
    operator fun rem(other: Variable<T>) {
        parent.children.add(RawJs("$name % $other"))
    }

    override fun render(): String {
        return jsReturn
    }
}

inline fun <reified T> JavaScript.const(value: T, name: String): Const<T> {
    val constant = Const(
        value = value,
        name = name,
        parent = this
    )
    children.add(constant)
    return constant
}