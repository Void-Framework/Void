package io.void.js.keywords

data class Call<T: Keyword>(val variableName: String, val callableMethod: T.() -> Any, val clazz: T): Keyword {

    init {
        clazz.callableMethod()
    }

    override var jsReturn: String = "$variableName${if (clazz.jsReturn.startsWith(".")){
        ""
    } else {
        "."
    }
    }${clazz.jsReturn}"

    override fun render(): String {
        return jsReturn
    }
}