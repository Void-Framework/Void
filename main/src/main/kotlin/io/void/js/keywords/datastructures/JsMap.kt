package io.void.js.keywords.datastructures

import io.void.js.JavaScript
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

    fun set(key: K, value: V): JsMap<K, V> {
        jsReturn += ".set($key, $value)"
        return this@JsMap
    }
    fun get(key: K): JsMap<K, V> {
        jsReturn += ".get($key)"
        return this@JsMap
    }
    fun has(key: K): JsMap<K, V> {
        jsReturn += ".has($key)"
        return this@JsMap
    }
    fun delete(key: K): JsMap<K, V> {
        jsReturn += ".delete($key)"
        return this@JsMap
    }
    fun clear(): JsMap<K, V> {
        jsReturn += ".clear()"
        return this@JsMap
    }
    fun size(): JsMap<K, V> {
        jsReturn += ".size"
        return this@JsMap
    }
    fun entries(): JsList<JsList<Any?>> {
        jsReturn += ".entries()"
        return JsList(listOf(JsList(listOf())))
    }
}

inline fun <reified K, reified V> JavaScript.map(baseMap: Map<K, V>): JsMap<K, V> {
    val map = JsMap(baseMap = baseMap).initialize()
    children.add(map)
    return map as JsMap<K, V>
}