package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.keywords.Call
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword

data class EventFunction(
    val _body: JavaScript.(Function) -> Unit,
    val stopReload: Boolean = false,
    val js: JavaScript,
    val eventValueName: String
): Function(
    name = "",
    arguments = listOf(eventValueName),
    body = _body,
) {

    init {
        body(js, this)
        if (stopReload) {
            children.addFirst(Call<Function>("event", "preventDefault()"))
        }
    }

    override fun render(): String {
        return "($eventValueName) => {${children.joinToString(";") { it.render() }}}"
    }
}

fun JavaScript.eFunction(body : JavaScript.(Function) -> Unit, stopReload: Boolean = false, eventValueName: String): EventFunction {
    val function = EventFunction(
        _body = body,
        stopReload = stopReload,
        js = this,
        eventValueName = eventValueName
    )
    children.add(function)
    return function
}

data class Event(val eventType: CustomEvent, val function: EventFunction): Keyword {

    override var jsReturn: String = ".addEventListener(\"${eventType.eventName.lowercase()}\", ${function.render()})"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.listen(_eventType: CustomEvent, _function: EventFunction): Event {
    val event = Event(
        eventType = _eventType,
        function = _function
    )
    children.add(event)
    return event
}