package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Function
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
    fun get(key: K): Void {
        jsReturn += ".get($key)"
        return Void()
    }
    fun has(key: K): Void {
        jsReturn += ".has($key)"
        return Void()
    }
    fun delete(key: K): Void {
        jsReturn += ".delete($key)"
        return Void()
    }
    fun clear(): Void {
        jsReturn += ".clear()"
        return Void()
    }
    fun size(): Void {
        jsReturn += ".size"
        return Void()
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
    return map as JsMap<K, V>
}