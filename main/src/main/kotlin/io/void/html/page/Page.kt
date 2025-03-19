package io.void.html.page

import io.void.html.Element
import io.void.js.EventDispatcher
import io.void.js.data.DataHolder
import io.void.ws.WSClient

abstract class Page(open val target: String) {

    abstract var content: Element?
    var client: WSClient? = null
        set(value) {
            field = value
            // Update all DataHolder instances with the new client
            updateDataHolders(content, value)
        }

    private fun updateDataHolders(element: Element?, client: WSClient?) {
        if (element == null) return

        if (element is DataHolder<*>) {
            element.setClient(client!!)
        }

        element.children?.forEach { child ->
            updateDataHolders(child, client)
        }
    }

    init {
        EventDispatcher.addEventListener(DataHolder::class.java) {
            println(it.get())
        }
    }
}