package io.void.html

import io.void.api.method.Method
import io.void.dto.http.Headers
import io.void.dto.http.RequestDTO

typealias Attribute = Pair<String, String>

abstract class Element internal constructor(
    open val name: String,
) {
    open val children: MutableList<Element>? = mutableListOf()
    val attributes = mutableListOf<Attribute>()

    abstract fun render(): String

    fun addAttributes(vararg _attributes: Attribute) {
        attributes.addAll(_attributes)
    }

    fun findElement(query: String): Element? {
        val attr = when (query[0]) {
            '#' -> "id"
            '.' -> "class"
            else -> return null
        }
        val attrValue = query.substring(1)

        if (attributes.any { it.first == attr && it.second == attrValue }) return this

        children?.forEach { child ->
            val found = child.findElement(query)
            if (found != null) return found
        }

        return null
    }

    operator fun get(attrName: String): String? = attributes.firstOrNull { it.first == attrName }?.second
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

class KtsBuilder(private val element: Element) {

    // --- Requests ---
    fun on(url: String, method: Method) =
        element.attributes.add("kts-${method.name.lowercase()}" to url)

    fun target(selector: String) =
        element.attributes.add("kts-target" to selector)

    fun swap(strategy: String) =
        element.attributes.add("kts-swap" to strategy)

    fun trigger(event: String) =
        element.attributes.add("kts-trigger" to event)

    fun include(selector: String) =
        element.attributes.add("kts-include" to selector)

    fun params(value: String) =
        element.attributes.add("kts-params" to value)

    fun vals(json: String) =
        element.attributes.add("kts-vals" to json)

    fun headers(values: Headers) =
        element.attributes.add("kts-headers" to values.entries.joinToString(
            prefix = "{", postfix = "}"
        ) { "\"${it.key}\": \"${it.value}\"" })


    fun request(dto: RequestDTO) =
        element.attributes.add(
            "kts-request" to buildString {
                append("{")
                append("\"method\":\"${dto.method.name}\",")
                append("\"target\":\"${dto.target}\"")
                if (dto.headers.isNotEmpty()) {
                    append(",\"headers\":{")
                    append(dto.headers.entries.joinToString(",") {
                        "\"${it.key}\":\"${it.value}\""
                    })
                    append("}")
                }
                if (dto.body.isNotEmpty()) {
                    append(",\"body\":\"${dto.body}\"")
                }
                append("}")
            }
        )

    fun encoding(value: String) =
        element.attributes.add("kts-encoding" to value)

    fun select(selector: String) =
        element.attributes.add("kts-select" to selector)

    fun swapOob(value: String) =
        element.attributes.add("kts-swap-oob" to value)

    fun confirm(message: String) =
        element.attributes.add("kts-confirm" to message)

    fun disable() =
        element.attributes.add("kts-disable" to "true")

    fun disabledElt(selector: String) =
        element.attributes.add("kts-disabled-elt" to selector)

    fun disinherit(value: String) =
        element.attributes.add("kts-disinherit" to value)

    fun boost(enable: Boolean = true) =
        element.attributes.add("kts-boost" to enable.toString())

    fun history(value: String) =
        element.attributes.add("kts-history" to value)

    fun historyElt(selector: String) =
        element.attributes.add("kts-history-elt" to selector)

    fun ext(name: String) =
        element.attributes.add("kts-ext" to name)

    fun onEvent(event: String, script: String) =
        element.attributes.add("kts-on:$event" to script)
}

