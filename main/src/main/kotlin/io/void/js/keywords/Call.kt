package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.variable.Variable

@ConsistentCopyVisibility
data class Call<T: Keyword> internal constructor(val variableName: String): Keyword {

    override var jsReturn = ""

    constructor(variableName: JsValue<*>, callableFunction: String): this(variableName.toString()) {
        jsReturn = "$variableName.$callableFunction"
    }

    constructor(variableName: JsValue<*>, callableMethod: T.() -> Any, clazz: T): this(variableName.toString()) {
        clazz.callableMethod()
        jsReturn = "$variableName${if (clazz.jsReturn.startsWith(".")){
            ""
        } else {
            "."
        }
        }${clazz.jsReturn}"
    }

    override fun render(): String {
        return jsReturn
    }
}

inline fun <reified T : Keyword> JavaScript.call(variableName: JsValue<*>, callableFunction: String): Call<T> {
    val call = Call<T>(
        variableName = variableName,
        callableFunction = callableFunction
    )
    children.add(call)
    return call
}

inline fun <reified T : Keyword> JavaScript.call(variableName: JsValue<*>, noinline callableMethod: T.() -> Any, clazz: T): Call<T> {
    val call = Call(
        variableName = variableName,
        callableMethod = callableMethod,
        clazz = clazz
    )
    children.add(call)
    return call
}