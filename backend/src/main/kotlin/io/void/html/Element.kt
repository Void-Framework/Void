package io.void.html

import androidx.compose.runtime.Composable
import io.void.api.method.Method
import io.void.dto.http.RequestDTO
//import io.void.generated.Div
//import io.void.generated.H2
import java.util.Locale.getDefault

/**
 * Represents an HTML attribute as a name/value pair (e.g. "id" to "main").
 */
typealias Attribute = Pair<String, String>

/**
 * Base type for all HTML-like nodes in the Void DSL.
 *
 * An Element has a tag/name, optional attributes, and optional child elements.
 * Subclasses implement [render] to produce a string representation.
 */
abstract class Element internal constructor(
    open val name: String,
) {
    /** Optional list of child elements (null means this node cannot have children). */
    open val children: MutableList<Element>? = mutableListOf()

    /** Mutable list of element attributes (name/value pairs). */
    val attributes = mutableListOf<Attribute>()

    /**
     * Renders this element and all its children to an HTML string (or DSL string).
     */
    abstract fun render(): String

    /**
     * Adds one or more attributes to this element.
     *
     * Example: addAttributes("id" to "main", "class" to "btn")
     */
    fun addAttributes(vararg _attributes: Attribute) {
        attributes.addAll(_attributes)
    }

    /**
     * Recursively searches this element and its descendants for a node whose
     * id or class matches the CSS-like [query].
     *
     * Accepts:
     * - "#idValue" to match by id
     * - ".classValue" to match by class
     * Returns the first matching [Element] or null if none found.
     */
    fun findElement(query: String): Element? {
        val attr =
            when (query[0]) {
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

    /**
     * Returns the value of the attribute with the given [attrName], or null if absent.
     */
    operator fun get(attrName: String): String? = attributes.firstOrNull { it.first == attrName }?.second

    /**
     * Returns the rendered string of this element. Equivalent to [render].
     */
    override fun toString(): String = render()

    /**
     * Adds a text node to this element using a concise unary plus operator.
     *
     * Example: +"Hello" will append a [Fractal] text node as a child.
     */
    operator fun String.unaryPlus() {
        children!!.add(Fractal(text = this))
    }
}

/**
 * Repeats [element] for each index in [range] and returns a [Fractal] fragment holding the produced children.
 */
@Composable
fun Element.loop(
    range: IntRange,
    element: @Composable Element.(Int) -> Unit,
): Fractal {
    val fragment =
        Fractal {
            for (i in range) {
                element(i)
            }
        }
    return fragment
}

/**
 * Shorthand for a container div with sensible default Tailwind classes.
 * You can override/extend classes via [attrs].
 */
/*@Composable
fun Element.Container(
    vararg attrs: Pair<String, String>,
    content: @Composable Element.() -> Unit,
) = Div("class" to "container mx-auto px-4", *attrs, _children = content)

/**
 * Shorthand for a flex container div with default alignment.
 */
@Composable
fun Element.Flex(
    vararg attrs: Pair<String, String>,
    content: @Composable Element.() -> Unit,
) = Div("class" to "flex items-center", *attrs, _children = content)

/**
 * Shorthand for a flex container centered both horizontally and vertically.
 */
@Composable
fun Element.Center(
    vararg attrs: Pair<String, String>,
    content: @Composable Element.() -> Unit,
) = Div("class" to "flex justify-center items-center", *attrs, _children = content)

/**
 * Section helper that renders a titled block using an H2 followed by custom [content].
 */
@Composable
fun Element.Section(
    title: String,
    vararg attrs: Pair<String, String>,
    content: @Composable Element.() -> Unit,
) = Div("class" to "mb-12", *attrs) {
    H2("class" to "text-2xl font-semibold mb-4") { Fractal(title) }
    content()
}*/

/**
 * Enables the KTS attribute DSL on this element. Usage:
 * element.kts { on("/path", Method.GET); target("#content") }
 */
fun Element.kts(block: KtsBuilder.() -> Unit): Element {
    KtsBuilder(this).apply(block)
    return this
}

/**
 * DSL builder that attaches kts-* attributes to an [Element] to control client-side behavior.
 * Methods map to specific attributes like "kts-get", "kts-target", etc.
 */
class KtsBuilder(
    private val element: Element,
) {
    // --- Requests ---
    /** Adds a kts-* attribute for the given HTTP [method] pointing to [url] (e.g., kts-get, kts-post). */
    fun on(
        url: String,
        method: Method,
    ) = element.attributes.add("kts-${method.name.lowercase()}" to url)

    /** Sets the CSS selector of the target element to update. */
    fun target(selector: String) = element.attributes.add("kts-target" to selector)

    /** Sets the swap strategy (e.g., innerHTML, outerHTML, beforeend, etc.). */
    fun swap(strategy: String) = element.attributes.add("kts-swap" to strategy)

    /** Triggers the request on a given DOM [event] (e.g., click, submit). */
    fun trigger(event: String) = element.attributes.add("kts-trigger" to event)

    /** Includes a sub-selection from the response using a CSS [selector]. */
    fun include(selector: String) = element.attributes.add("kts-include" to selector)

    /** Adds URL/form parameters. Value format depends on your client implementation. */
    fun params(value: String) = element.attributes.add("kts-params" to value)

    /** Supplies raw JSON values to send along with the request. */
    fun vals(json: String) = element.attributes.add("kts-vals" to json)

    /**
     * Adds custom request headers. They will be serialized into the kts-headers attribute
     * as a JSON object.
     */
    fun headers(values: MutableMap<String, String>) =
        element.attributes.add(
            "kts-headers" to
                values.entries.joinToString(
                    prefix = "{",
                    postfix = "}",
                ) { "\"${it.key}\": \"${it.value}\"" },
        )

    /** Sets a full request description via a [RequestDTO], serialized into kts-request. */
    fun request(dto: RequestDTO) =
        element.attributes.add(
            "kts-request" to
                buildString {
                    append("{")
                    append("\"method\":\"${dto.method.name}\",")
                    append("\"target\":\"${dto.target}\"")
                    if (dto.headers.isNotEmpty()) {
                        append(",\"headers\":{")
                        append(
                            dto.headers.entries.joinToString(",") {
                                "\"${it.key}\":\"${it.value}\""
                            },
                        )
                        append("}")
                    }
                    if (dto.body.isNotEmpty()) {
                        append(",\"body\":\"${dto.body}\"")
                    }
                    append("}")
                },
        )

    /** Sets request body encoding (e.g., application/json). */
    fun encoding(value: String) = element.attributes.add("kts-encoding" to value)

    /** Selects a sub-tree from the current document to apply updates to. */
    fun select(selector: String) = element.attributes.add("kts-select" to selector)

    /** Marks an out-of-band swap operation. */
    fun swapOob(value: String) = element.attributes.add("kts-swap-oob" to value)

    /** Displays a confirmation [message] before performing the action. */
    fun confirm(message: String) = element.attributes.add("kts-confirm" to message)

    /** Disables the element while the request is in-flight. */
    fun disable() = element.attributes.add("kts-disable" to "true")

    /** CSS selector for an element to disable while the request is in-flight. */
    fun disabledElt(selector: String) = element.attributes.add("kts-disabled-elt" to selector)

    /** Controls inheritance of kts-* attributes in the DOM. */
    fun disinherit(value: String) = element.attributes.add("kts-disinherit" to value)

    /** Enables or disables boost navigation behavior. */
    fun boost(enable: Boolean = true) = element.attributes.add("kts-boost" to enable.toString())

    /** Controls history behavior (e.g., push, replace). */
    fun history(value: String) = element.attributes.add("kts-history" to value)

    /** Sets the element used to manage history state. */
    fun historyElt(selector: String) = element.attributes.add("kts-history-elt" to selector)

    /** Registers a client-side extension name to be used. */
    fun ext(name: String) = element.attributes.add("kts-ext" to name)

    /** Binds an inline client-side [script] to a DOM [event] (kts-on:event). */
    fun onEvent(
        event: String,
        script: String,
    ) = element.attributes.add("kts-on:$event" to script)

    /** Prevents the default event behavior.. */
    fun prevent(value: Boolean) = element.attributes.add("kts-prevent" to value.toString().lowercase(getDefault()))
}
