package io.void.html.page

import io.void.html.Element
import io.void.js.EventDispatcher
import io.void.js.data.DataHolder

abstract class Page(open val target: String) {

    abstract var content: Element?

    init {
        EventDispatcher.addEventListener(DataHolder::class.java) {
            println(it.get())
        }
    }
}