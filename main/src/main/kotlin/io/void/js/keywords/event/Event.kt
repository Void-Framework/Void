package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.keywords.*
import io.void.js.keywords.Function
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.event.exception.FunctionNotVariableException
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
                children.addFirst(Call<Function>(eventValueName.asJsValue(), "preventDefault()"))
            }
            if (!propagate) {
                children.addFirst(Call<Function>(eventValueName.asJsValue(), "stopPropagation()"))
            }
            if (!immediatePropagation) {
                children.addFirst(Call<Function>(eventValueName.asJsValue(), "stopImmediatePropagation()"))
            }
        }
    }

    fun phase(): Void {
        put(InlineCall(operation = "$eventValueName.eventPhase"))
        return Void()
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

data class Event(val eventType: JsValue<CustomEvent>, val function: JsValue<EventFunction>): Keyword {
    var bubble = false
    private var isVariable = false
    lateinit var variable: Variable<EventFunction>

    init {
        isVariable = function is VariableValue<EventFunction>
    }

    override var jsReturn: String = ".addEventListener($eventType,${if (!isVariable) function.toJs() else variable.name},${if (!bubble) ", true" else ""})"

    override fun render(): String {
        return jsReturn
    }
}