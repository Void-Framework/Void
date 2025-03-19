package io.void.js.data

import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import kotlin.reflect.KClass

class DataHolder<T>(private var value: T?): Fractal() {

    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf()
    override val allowedAttributes: List<AttributeNames> = listOf()

    override fun render(): String {
        return value as String
    }

    fun set(newValue: T): T? {
        value = newValue
        DataHandler.singleton.update(
            newData = this
        )
        return value
    }

    fun get(): T? {
        return value
    }
}

inline fun<reified T> Element.DataHolder(_value: T?): DataHolder<T> {
    val holder = DataHolder(
        value = _value
    )
    children!!.add(holder)
    return holder
}