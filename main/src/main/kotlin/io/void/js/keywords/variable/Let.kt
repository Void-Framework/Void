package io.void.js.keywords.variable

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.RawJs
import io.void.js.keywords.raw

data class Let<T>(
    override val value: T?,
    override val name: String,
    val parent: JavaScript
) : Variable<T> {

    override var jsReturn: String = "let $name = ${if (value is Keyword) value.render() else "$value"}"

    override fun render(): String = jsReturn

    operator fun inc(): Let<T> {
        parent.children.add(RawJs("$name++"))
        return this
    }
    operator fun dec(): Let<T> {
        parent.children.add(RawJs("$name--"))
        return this
    }
    operator fun plus(other: JsValue<T>) {
        parent.children.add(RawJs("$name + $other"))
    }
    operator fun minus(other: JsValue<T>) {
        parent.children.add(RawJs("$name - $other"))
    }
    operator fun times(other: JsValue<T>) {
        parent.children.add(RawJs("$name * $other"))
    }
    operator fun div(other: JsValue<T>) {
        parent.children.add(RawJs("$name / $other"))
    }
    operator fun rem(other: JsValue<T>) {
        parent.children.add(RawJs("$name % $other"))
    }
}


class Setter<T>(newValue: T, variable: Let<T>): Keyword {

    override var jsReturn: String = "${variable.name} = ${if (newValue is Keyword) {
        newValue.render()
    } else {
        "$newValue"
    }
    }"

    override fun render(): String {
        return this.jsReturn
    }

}

inline fun <reified T> JavaScript.let(value: T, name: String): Let<T> {
    val let = Let(value = value, name = name, parent = this)
    children.add(let)
    return let
}
inline fun <reified T> JavaScript.set(value: Let<T>, newValue: T): Setter<T> {
    val set = Setter(newValue, value)
    children.add(set)
    return set
}