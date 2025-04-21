package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.variable.Variable

@ConsistentCopyVisibility
data class Call<T: Keyword> internal constructor(val variableName: String): Keyword {

    override var jsReturn = ""

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

    constructor(variable: Variable<*>, callableMethod: T.() -> Any, clazz: T): this(variable.name) {
        clazz.callableMethod()
        jsReturn = "$variableName${if (clazz.jsReturn.startsWith(".")){
            ""
        } else {
            "."
        }
        }${clazz.jsReturn}"
    }

    constructor(variable: Variable<T>, callableFunction: String): this(variable.name) {
        jsReturn = "$variableName.$callableFunction"
    }

    constructor(function: Function, argsList: List<String> = emptyList(), callableMethod: T.() -> Any, clazz: T): this(function.run(argsList)) {
        clazz.callableMethod()
        jsReturn = "$variableName${if (clazz.jsReturn.startsWith(".")){
            ""
        } else {
            "."
        }
        }${clazz.jsReturn}"
    }

    constructor(function: Function, argsList: List<String> = emptyList(), callableFunction: String): this(function.run(argsList)) {
        jsReturn = "$variableName.$callableFunction"
    }

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

inline fun <reified T : Keyword> JavaScript.call(variable: Variable<T>, callableFunction: String): Call<T> {
    val call = Call<T>(
        variable = variable,
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

inline fun <reified T : Keyword> JavaScript.call(function: Function, argsList: List<String> = emptyList(), callableFunction: String): Call<T> {
    val call = Call<T>(
        function = function,
        argsList = argsList,
        callableFunction = callableFunction
    )
    children.add(call)
    return call
}

inline fun <reified T : Keyword> JavaScript.call(function: Function, argsList: List<String> = emptyList(), noinline callableMethod: T.() -> Any, clazz: T): Call<T> {
    val call = Call(
        function = function,
        argsList = argsList,
        callableMethod = callableMethod,
        clazz = clazz
    )
    children.add(call)
    return call
}

inline fun <reified T : Keyword> JavaScript.call(variable: Variable<*>, noinline callableMethod: T.() -> Any, clazz: T): Call<T> {
    val call = Call(
        variable = variable,
        callableMethod = callableMethod,
        clazz = clazz
    )
    children.add(call)
    return call
}