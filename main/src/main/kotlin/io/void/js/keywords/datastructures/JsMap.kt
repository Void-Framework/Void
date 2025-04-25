package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.Function
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword

data class JsMap<K, V>(val baseMap: Map<K, V>): JsDatastructure {

    override var jsReturn: String = ""
    private var inside = StringBuilder("")

    init {
        baseMap.forEach { (key, value) ->
            inside.append("[$key, $value],")
        }
        if (baseMap.isNotEmpty()) {
            inside.setLength(inside.length - 1)
        }
    }

    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "new Map([$inside])"
        return this
    }

    fun set(key: JsValue<K>, value: JsValue<V>): JsMap<K, V> {
        jsReturn += ".set($key, $value)"
        return this@JsMap
    }
    fun get(key: JsValue<K>): JsMap<K, V> {
        jsReturn += ".get($key)"
        return this
    }
    fun has(key: JsValue<K>): JsMap<K, V> {
        jsReturn += ".has($key)"
        return this
    }
    fun delete(key: JsValue<K>): JsMap<K, V> {
        jsReturn += ".delete($key)"
        return this
    }
    fun clear(): JsMap<K, V> {
        jsReturn += ".clear()"
        return this
    }
    fun size(): JsMap<K, V> {
        jsReturn += ".size"
        return this
    }
    fun entries(): JsMap<K, V> {
        jsReturn += ".entries()"
        return this
    }
}

inline fun <reified K, reified V> JavaScript.map(baseMap: Map<K, V>): JsMap<K, V> {
    val map = JsMap(
        baseMap = baseMap,
    ).initialize()
    children.add(map)
    @Suppress("UNCHECKED_CAST")
    return map as JsMap<K, V>
}