package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.keywords.Const
import io.void.js.keywords.Keyword

data class CustomEvent(val eventName: String): Keyword {

    val variable: JavaScript.Variable.Constant = JavaScript.Variable.Constant(Const(name = DataHandler.randomString(5), value = "new Event(\"$eventName\")"))
    override var jsReturn: String = variable.variable.render()

    companion object {
        private val defaultEvents: Map<Events, CustomEvent> by lazy {
            Events.entries.associateWith { CustomEvent(it.name.lowercase()) }.toMap()
        }

        fun getEvent(events: Events): CustomEvent {
            return defaultEvents[events]!!
        }
    }

    override fun render(): String {
        return "$jsReturn;"
    }
}
