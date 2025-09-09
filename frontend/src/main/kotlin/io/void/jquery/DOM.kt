@file:JsExport
@file:OptIn(ExperimentalJsExport::class)

package io.void.jquery

import kotlinx.browser.document
import org.w3c.dom.DOMTokenList
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.asList
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.events.Event
import org.w3c.dom.get

@JsName("$")
fun select(element: String): DOMWrapper {
    val nodeList = document.querySelectorAll(element)
    val elements = (0 until nodeList.length).map { nodeList.item(it)!! }
        .mapNotNull { it as? Element }
    return DOMWrapper(elements)
}

class DOMWrapper(
    private val elements: List<Element>
) {

    fun text(text: String): DOMWrapper {
        elements.forEach { (it as? HTMLElement)?.innerText = text }
        return this
    }

    @JsName("textReturn")
    val text: String? = (elements.firstOrNull() as? HTMLElement)?.innerText

    fun html(element: HTMLElement): DOMWrapper {
        elements.forEach { (it as? HTMLElement)?.innerHTML = element.innerHTML }
        return this
    }

    @JsName("htmlReturn")
    val html: String? = (elements.firstOrNull() as? HTMLElement)?.innerHTML

    fun value(v: String): DOMWrapper {
        elements.forEach {
            when (it) {
                is HTMLInputElement -> it.value = v
                is HTMLTextAreaElement -> it.value = v
                is HTMLSelectElement -> it.value = v
            }
        }
        return this
    }

    @JsName("valReturn")
    var value: String? = when (val element = elements.firstOrNull()) {
        is HTMLInputElement -> element.value
        is HTMLTextAreaElement -> element.value
        is HTMLSelectElement -> element.value
        else -> null
    }

    fun attr(name: String): String? = elements.firstOrNull()?.getAttribute(name)

    @JsName("setAttr")
    fun attr(name: String, value: String) {
        elements.forEach {
            it.setAttribute(name, value)
        }
    }

    fun removeAttr(name: String) {
        elements.forEach {
            it.removeAttribute(name)
        }
    }

    fun classes(): DOMTokenList? = elements.firstOrNull()?.classList

    @JsName("setProperty")
    fun property(property: String, value: String) {
        style()?.setProperty(property, value)
    }

    fun style(): CSSStyleDeclaration? = (elements.firstOrNull() as? HTMLElement)?.style

    @JsName("getProperty")
    fun property(property: String): String? = style()?.getPropertyValue(property)

    fun on(event: String, lambda: (Event) -> Unit) {
        elements.forEach {
            it.addEventListener(event, lambda)
        }
    }

    fun off(event: String, lambda: ((Event) -> Unit)? = null) {
        elements.forEach {
            it.removeEventListener(event, lambda)
        }
    }

    val parent = elements.firstOrNull()?.parentElement
    val children = elements.firstOrNull()?.children
    val siblings = elements.firstOrNull()?.parentElement?.children

    fun remove() {
        elements.forEach { it.remove() }
    }

    fun empty() {
        elements.forEach { it.innerHTML = "" }
    }

    fun hide() {
        elements.forEach { _ ->
            style()?.display = "none"
        }
    }

    fun show() {
        elements.forEach { _ ->
            style()?.display = ""
        }
    }

    fun find(selector: String): DOMWrapper =
        DOMWrapper(elements.flatMap {
            it.querySelectorAll(selector).asList().mapNotNull { it as? Element }
        })

    fun closest(selector: String): DOMWrapper =
        DOMWrapper(elements.mapNotNull { it.closest(selector) })

    @JsName("childByName")
    fun children(selector: String? = null): DOMWrapper =
        DOMWrapper(elements.flatMap { parent ->
            val list = parent.children.asList()
            if (selector != null) list.filter { it.matches(selector) } else list
        })

    @JsName("siblingByName")
    fun siblings(selector: String? = null): DOMWrapper =
        DOMWrapper(elements.flatMap { el ->
            el.parentElement?.children?.asList()?.filter { it != el }?.let { list ->
                if (selector != null) list.filter { it.matches(selector) } else list
            } ?: emptyList()
        })

    fun next(): DOMWrapper =
        DOMWrapper(elements.mapNotNull { it.nextElementSibling })

    fun prev(): DOMWrapper =
        DOMWrapper(elements.mapNotNull { it.previousElementSibling })

    fun append(element: HTMLElement): DOMWrapper {
        elements.forEach { it.appendChild(element.cloneNode(true)) }
        return this
    }

    fun prepend(element: HTMLElement): DOMWrapper {
        elements.forEach { it.insertBefore(element.cloneNode(true), it.firstChild) }
        return this
    }

    fun before(element: HTMLElement): DOMWrapper {
        elements.forEach { it.parentElement?.insertBefore(element.cloneNode(true), it) }
        return this
    }

    fun after(element: HTMLElement): DOMWrapper {
        elements.forEach { it.parentElement?.insertBefore(element.cloneNode(true), it.nextSibling) }
        return this
    }

    fun addClass(name: String) {
        elements.forEach { it.classList.add(name) }
    }

    fun removeClass(name: String) {
        elements.forEach { it.classList.remove(name) }
    }

    fun toggleClass(name: String) {
        elements.forEach { it.classList.toggle(name) }
    }

    fun one(event: String, handler: (Event) -> Unit) {
        elements.forEach { el ->
            val wrapper: (Event) -> Unit = {
                handler(it)
                el.removeEventListener(event, {})
            }
            el.addEventListener(event, wrapper)
        }
    }

    fun trigger(event: String) {
        elements.forEach { el ->
            val ev = Event(event)
            el.dispatchEvent(ev)
        }
    }

    fun each(lambda: (Element, Int) -> Unit) {
        elements.forEachIndexed { idx, el -> lambda(el, idx) }
    }

}

