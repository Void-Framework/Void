package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.keywords.Call
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.variable.Variable

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
    var propagate = true
    var immediatePropagation = true

    init {
        body(js, this)
        if (eventValueName.isNotBlank()) {
            if (stopReload) {
                children.addFirst(Call<Function>(eventValueName, "preventDefault()"))
            }
            if (!propagate) {
                children.addFirst(Call<Function>(eventValueName, "stopPropagation()"))
            }
            if (!immediatePropagation) {
                children.addFirst(Call<Function>(eventValueName, "stopImmediatePropagation()"))
            }
        }
    }

    override var jsReturn: String = "($eventValueName) => {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
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
    var bubble = false
    private var isVariable = false
    private lateinit var variable: Variable<EventFunction>

    override var jsReturn: String = ".addEventListener(\"${eventType.eventName.lowercase()}\", ${if (!isVariable) function.render() else variable.name}${if (!bubble) ", true" else ""})"

    override fun render(): String {
        return jsReturn
    }

    constructor(eventType: CustomEvent, variable: Variable<EventFunction>): this(
        eventType = eventType,
        function = variable.value!!
    ) {
        isVariable = true
        this.variable = variable
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
fun JavaScript.listen(_eventType: List<CustomEvent>, _function: EventFunction): List<Event> {
    return _eventType.map {
        val event = Event(
            eventType = it,
            function = _function
        )
        children.add(event)
        return@map event
    }
}
fun JavaScript.listen(_eventType: CustomEvent, variable: Variable<EventFunction>): Event {
    val event = Event(
        eventType = _eventType,
        variable = variable
    )
    children.add(event)
    return event
}
fun JavaScript.listen(_eventType: List<CustomEvent>, variable: Variable<EventFunction>): List<Event> {
    return _eventType.map {
        val event = Event(
            eventType = it,
            variable = variable
        )
        children.add(event)
        return@map event
    }
}