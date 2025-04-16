package io.void.js.keywords

import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.event.CustomEvent
import io.void.js.keywords.event.Event
import io.void.js.keywords.event.EventFunction
import io.void.js.keywords.event.exception.FunctionNotVariableException
import io.void.js.keywords.variable.Variable

interface BrowserObject: Keyword {

    fun on(_eventType: CustomEvent, _function: EventFunction): Event {
        val event = Event(
            eventType = _eventType,
            function = _function
        )
        jsReturn += event.render()
        return event
    }
    fun on(_eventType: List<CustomEvent>, _function: EventFunction): List<Event> {
        return _eventType.map {
            return@map on(it, _function)
        }
    }
    fun on(_eventType: CustomEvent, variable: Variable<EventFunction>): Event {
        val event = Event(
            eventType = _eventType,
            variable = variable
        )
        jsReturn += event.render()
        return event
    }
    fun on(_eventType: List<CustomEvent>, variable: Variable<EventFunction>): List<Event> {
        return _eventType.map {
            return@map on(it, variable)
        }
    }
    fun off(event: Event): Void {
        try {
            jsReturn += InlineCall(
                operation = ".removeEventListener(\"${event.eventType.eventName}\", ${event.variable.name}${if (!event.bubble) ", true" else ""})"
            ).render()
        } catch (e: Exception) {
            throw FunctionNotVariableException(event = event)
        }
        return Void()
    }
    fun off(events: List<Event>): Void {
        events.forEach {
            try {
                off(it)
            } catch (e: Exception) {
                throw FunctionNotVariableException(event = it)
            }
        }
        return Void()
    }
}