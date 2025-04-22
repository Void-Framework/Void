package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.asJsValue
import io.void.js.keywords.variable.Const

interface JsEvent: Keyword
data class CustomEvent(val eventName: JsValue<*>): JsEvent {

    val variable = Const(name = DataHandler.randomString(5), value = "new Event($eventName)")
    override var jsReturn: String = variable.render()

    companion object {
        private val defaultEvents: Map<Events, DefaultEvent> by lazy {
            Events.entries.associateWith { DefaultEvent(it.name.lowercase()) }.toMap()
        }

        fun getEvent(events: Events): DefaultEvent {
            return defaultEvents[events]!!
        }
    }

    override fun render(): String {
        return jsReturn
    }
}

data class DefaultEvent(val eventName: String): JsEvent {
    override var jsReturn: String = "\"$eventName\""

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.customEvent(name: JsValue<*>): CustomEvent {
    val event = CustomEvent(name)
    children.add(event)
    return event
}
