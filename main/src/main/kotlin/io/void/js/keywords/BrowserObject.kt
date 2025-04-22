package io.void.js.keywords

import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.event.CustomEvent
import io.void.js.keywords.event.Event
import io.void.js.keywords.event.EventFunction
import io.void.js.keywords.event.exception.FunctionNotVariableException
import io.void.js.keywords.variable.Variable
import java.lang.UnsupportedOperationException

interface BrowserObject: Keyword {

    fun on(_eventType: JsValue<CustomEvent>, _function: JsValue<EventFunction>): Event {
        val event = Event(
            eventType = _eventType,
            function = _function
        )
        jsReturn += event.render()
        return event
    }
    fun on(_eventType: List<JsValue<CustomEvent>>, _function: JsValue<EventFunction>): List<Event> {
        return _eventType.map {
            return@map on(it, _function)
        }
    }
    fun off(event: JsValue<Event>): Void {
        val eventValue = when (event) {
            is DirectValue<Event> -> event.value
            is VariableValue<Event> -> event.variable.value!!
            else -> throw UnsupportedOperationException("Cannot use a js function")
        }
        try {
            jsReturn += InlineCall(
                operation = ".removeEventListener($event, ${eventValue.variable.name}${if (!eventValue.bubble) ", true" else ""})"
            ).render()
        } catch (e: Exception) {
            throw FunctionNotVariableException(event = eventValue)
        }
        return Void()
    }
    fun off(events: List<JsValue<Event>>): Void {
        events.forEach {
            off(it)
        }
        return Void()
    }
}