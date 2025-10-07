package io.void.html

import io.void.api.method.Method
import io.void.dto.http.Headers
import io.void.dto.http.RequestDTO
import java.util.UUID

typealias Attribute = Pair<String, String>
typealias Attributes = MutableMap<String, String>

abstract class Element internal constructor(
    open val name: String,
) {
    open val children: MutableList<Element>? = mutableListOf()
    val attributes: Attributes = mutableMapOf()

    abstract fun render(): String

    fun addAttributes(vararg _attributes: Attribute) {
        attributes.putAll(_attributes)
    }

    fun findElement(query: String): Element? {
        val attr =
            when (query[0]) {
                '#' -> "id"
                '.' -> "class"
                else -> return null
            }
        val attrValue = query.substring(1)

        if (attributes.any { it.key == attr && it.value == attrValue }) return this

        children?.forEach { child ->
            val found = child.findElement(query)
            if (found != null) return found
        }

        return null
    }

    operator fun get(attrName: String): String? = attributes[attrName]
    internal operator fun set(attrName: String, attrValue: String) = { attributes[attrName] = attrValue  }
}

fun Element.loop(
    range: IntRange,
    element: Element.(Int) -> Unit,
): Fractal {
    val fragment =
        Fractal {
            for (i in range) {
                element(i)
            }
        }
    return fragment
}

fun Element.kts(block: KtsBuilder.() -> Unit): Element {
    KtsBuilder(this).apply(block)
    return this
}

class KtsBuilder(
    private val element: Element,
) {
    // --- Requests ---
    fun on(url: String, method: Method) {
        element.attributes["kts-${method.name.lowercase()}"] = url
    }

    fun target(selector: String) {
        element.attributes["kts-target"] = selector
    }

    fun swap(strategy: String) {
        element.attributes["kts-swap"] = strategy
    }

    fun trigger(event: String) {
        element.attributes["kts-trigger"] = event
    }

    fun headers(values: Headers) =
        {
            element.attributes["kts-headers"] =
                        values.entries.joinToString(prefix = "{", postfix = "}") { "\"${it.key}\":\"${it.value}\"" }

        }

    fun confirm(message: String) {
        element.attributes["kts-confirm"] = message
    }

    fun indicator(selector: Element) {
        element.attributes["kts-indicator"] = selector["id"] ?: run {
            val uuid = UUID.randomUUID()
            selector["id"] = uuid.toString()
            uuid.toString()
        }
        element.children?.add(selector)
    }
}
