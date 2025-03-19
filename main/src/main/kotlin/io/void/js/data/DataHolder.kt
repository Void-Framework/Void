package io.void.js.data

import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.ws.WSClient
import kotlin.reflect.KClass

class DataHolder<T>(private var value: T?, var client: WSClient? = null): Fractal() {

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

    fun setNewClient(_client: WSClient) {
        client = _client
        value?.let { set(it) }
    }

    fun get(): T? {
        return value
    }
}