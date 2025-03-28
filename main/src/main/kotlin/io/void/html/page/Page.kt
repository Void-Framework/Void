package io.void.html.page

import io.void.html.Element
import io.void.js.BaseJSImplementation
import io.void.js.EventDispatcher
import io.void.js.data.DataHolder
import io.void.js.parser.KotlinParser

abstract class Page(open val target: String) {

    abstract var content: Element?
    val classAttributes: MutableMap<Element, List<String>> = mutableMapOf()
    abstract val browserCode: BaseJSImplementation.() -> Unit

    init {
        EventDispatcher.addEventListener(DataHolder::class.java) {
            println(it.get())
        }
    }

}