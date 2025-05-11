package io.void.js.keywords.event

import io.void.js.JavaScript
import io.void.js.keywords.*
import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.data.randomString
import io.void.js.keywords.variable.Variable

data class EventFunction(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
): Function<Nothing>(
    name = "",
    arguments = listOf(String.randomString(4)),
    body = _body,
) {
    var propagate = true
    var immediatePropagation = true
    var stopReload: Boolean = true
    val eventName = arguments[0]

    init {
        if (stopReload) {
            children.addFirst(Call<Function<Nothing>>(RawJs(eventName).asJsValue(), "preventDefault()"))
        }
        if (!propagate) {
            children.addFirst(Call<Function<Nothing>>(RawJs(eventName).asJsValue(), "stopPropagation()"))
        }
        if (!immediatePropagation) {
            children.addFirst(Call<Function<Nothing>>(RawJs(eventName).asJsValue(), "stopImmediatePropagation()"))
        }
    }

    fun phase(): JsValue<Int> {
        children.add(RawJs(operation = "$eventName.eventPhase"))
        return 0.asJsValue()
    }

    override var jsReturn: String = "($eventName) => {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}
fun JavaScript.listen(htmlElement: JsValue<HTMLElement>, eventType: JsValue<JsEvent>, function: JsValue<EventFunction>) {
    call(htmlElement, {}, Event(
        eventType,
        function
    ))
}
fun JavaScript.listen(htmlElement: JsValue<HTMLElement>, eventType: JsValue<JsEvent>, function: JavaScript.(List<FunctionVariable<*>>) -> Unit) {
    call(htmlElement, {}, Event(
        eventType,
        function
    ))
}
fun Function<Nothing>.asEventFunction(): JsValue<EventFunction> {
    return EventFunction(this.body).asJsValue()
}

data class Event(val eventType: JsValue<JsEvent>, val function: JsValue<EventFunction>): Keyword {
    var useCapture = false
    private var isVariable = false
    lateinit var variable: Variable<EventFunction>

    constructor(eventType: JsValue<JsEvent>, body: JavaScript.(List<FunctionVariable<*>>) -> Unit): this(
        eventType,
        EventFunction(body).asJsValue()
    )

    init {
        isVariable = function is VariableValue<EventFunction>
    }

    override var jsReturn: String = ".addEventListener($eventType,${if (!isVariable) function.toJs() else variable.name}${if (useCapture) ", true" else ""})"

    override fun render(): String {
        return jsReturn
    }
}