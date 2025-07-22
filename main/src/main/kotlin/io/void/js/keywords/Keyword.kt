package io.void.js.keywords

private var _await: Boolean = false
private var _typeOf: Boolean = false

interface Keyword {

    var jsReturn: String
    fun render(): String

    var await: Boolean
        get() = _await
        set(value) {
            _await = value
        }
    var typeOf: Boolean
        get() = _await
        set(value) {
            _await = value
        }

    fun await(): Keyword {
        await = true
        return this
    }

    fun awaitRender(): String {
        return "await ${render()}"
    }

    fun typeOfRender(): String {
        return "typeof ${if (await) awaitRender() else render()}"
    }

    fun <M : Keyword, N : Keyword> applyMethods(call: (M) -> Unit, element: M, objectToRefer: N): Reference<N> {
        call(element)
        jsReturn += element.render()
        return objectToRefer.refer()
    }
    fun <M : Keyword, N : Keyword> applyNullableMethods(call: (M?) -> Unit, element: M?, objectToRefer: N): Reference<N> {
        call(element)
        jsReturn += element?.render()
        return objectToRefer.refer()
    }

    infix fun instanceOf(objectName: String) {
        jsReturn += " instanceof $objectName"
    }

    fun typeOf(): String {
        typeOf = true
        return ""
    }
}