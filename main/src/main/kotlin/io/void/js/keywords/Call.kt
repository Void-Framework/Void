package io.void.js.keywords

import io.void.js.JavaScript

data class Call<T: Keyword> internal constructor(val variableName: String): Keyword {

    constructor(variableName: String, callableFunction: String): this(variableName) {
        jsReturn = "$variableName.$callableFunction"
    }

    constructor(variableName: String, callableMethod: T.() -> Any, clazz: T): this(variableName) {
        clazz.callableMethod()
        jsReturn = "$variableName${if (clazz.jsReturn.startsWith(".")){
            ""
        } else {
            "."
        }
        }${clazz.jsReturn}"
    }

    override var jsReturn = ""

    override fun render(): String {
        return jsReturn
    }
}

inline fun <reified T : Keyword> JavaScript.call(variableName: String, callableFunction: String): Call<T> {
    val call = Call<T>(
        variableName = variableName,
        callableFunction = callableFunction
    )
    children.add(call)
    return call
}

inline fun <reified T : Keyword> JavaScript.call(variableName: String, noinline callableMethod: T.() -> Any, clazz: T): Call<T> {
    val call = Call(
        variableName = variableName,
        callableMethod = callableMethod,
        clazz = clazz
    )
    children.add(call)
    return call
}