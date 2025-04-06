package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class JsMap<K, V>(val baseMap: Map<K, V>): Keyword {

    override var jsReturn: String = ""
    private var inside = ""

    init {
        baseMap.forEach { (key, value) ->
            inside += "[$key, $value],"
        }
        if (baseMap.isNotEmpty()) {
            inside.replaceAfterLast(",", "")
        }
    }

    override fun render(): String {
        return jsReturn
    }

    fun initialize(): JsMap<K, V> {
        jsReturn = "new Map([$inside])"
        return this
    }

    inner class Actions {
        fun set(key: K, value: V) {
            jsReturn += ".set($key, $value)"
        }
        fun get(key: K) {
            jsReturn += ".get($key)"
        }
        fun has(key: K) {
            jsReturn += ".has($key)"
        }
        fun delete(key: K) {
            jsReturn += ".delete($key)"
        }
        fun clear() {
            jsReturn += ".clear()"
        }
        fun size() {
            jsReturn += ".size"
        }
    }
}

inline fun <reified K, reified V> JavaScript.map(baseMap: Map<K, V>): JsMap<K, V> {
    val map = JsMap(baseMap = baseMap)
    children.add(map)
    return map
}