package io.void.js.keywords

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