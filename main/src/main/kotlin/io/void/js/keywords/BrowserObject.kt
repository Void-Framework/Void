package io.void.js.keywords

import io.void.js.FunctionVariable
import io.void.js.JavaScript
import io.void.js.keywords.event.Event
import io.void.js.keywords.event.EventFunction
import io.void.js.keywords.event.JsEvent
import io.void.js.keywords.event.exception.FunctionNotVariableException
import java.lang.UnsupportedOperationException

interface BrowserObject: Keyword {

    fun on(_eventType: JsValue<JsEvent>, _function: JsValue<EventFunction>, call: (Event) -> Unit): Reference<BrowserObject> {
        val event = Event(
            eventType = _eventType,
            function = _function
        )
        jsReturn += event.render()
        return applyMethods(call, event, this)
    }
    fun on(_eventType: List<JsValue<JsEvent>>, _function: JsValue<EventFunction>, call: (Event) -> Unit): List<Reference<BrowserObject>> {
        return _eventType.map {
            return@map on(it, _function, call)
        }
    }
    fun off(event: JsValue<Event>): Reference<BrowserObject> {
        val eventValue = when (event) {
            is DirectValue<Event> -> event.value
            is VariableValue<Event> -> event.variable.value!!
            else -> throw UnsupportedOperationException("Cannot use a js function")
        }
        try {
            jsReturn += RawJs(
                operation = ".removeEventListener($event, ${eventValue.variable.name}${if (!eventValue.useCapture) ", true" else ""})"
            ).render()
        } catch (e: Exception) {
            throw FunctionNotVariableException(event = eventValue)
        }
        return this.refer()
    }
    fun off(events: List<JsValue<Event>>): Reference<BrowserObject> {
        events.forEach {
            off(it)
        }
        return this.refer()
    }
    fun on(_eventType: JsValue<JsEvent>, _function: JavaScript.(List<FunctionVariable<*>>) -> Unit, call: (Event) -> Unit): Reference<BrowserObject> {
        val event = Event(
            eventType = _eventType,
            body = _function
        )
        jsReturn += event.render()
        return applyMethods(call, event, this)
    }
    fun on(_eventType: List<JsValue<JsEvent>>, _function: JavaScript.(List<FunctionVariable<*>>) -> Unit, call: (Event) -> Unit): List<Reference<BrowserObject>> {
        return _eventType.map {
            return@map on(it, _function, call)
        }
    }
}