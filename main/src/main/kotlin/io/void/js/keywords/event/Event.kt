package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.keywords.Call
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

data class EventFunction(val _body: JavaScript.(Function) -> Unit, val stopReload: Boolean = false, val js: JavaScript): Function(
    name = "",
    arguments = listOf("event"),
    body = _body,
) {

    init {
        body(js, this)
        if (stopReload) {
            children.addFirst(Call<Function>("event", "preventDefault()"))
        }
    }

    override fun render(): String {
        println("${children.joinToString(";") { it.render() }}\n\n")
        return "function(event) {${children.joinToString(";") { it.render() }}}"
    }
}

fun JavaScript.eFunction(body : JavaScript.(Function) -> Unit, stopReload: Boolean = false): EventFunction {
    val function = EventFunction(
        _body = body,
        stopReload = stopReload,
        js = this
    )
    children.add(function)
    return function
}

data class Event(val eventType: Events, val function: EventFunction): Keyword {

    override var jsReturn: String = ".addEventListener(\"${eventType.name.lowercase()}\", ${function.render()})"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.listen(_eventType: Events, _function: EventFunction): Event {
    val event = Event(
        eventType = _eventType,
        function = _function
    )
    children.add(event)
    return event
}