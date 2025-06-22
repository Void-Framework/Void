package io.void.js.keywords

interface Keyword {

    var jsReturn: String
    fun render(): String
    var await: Boolean
        get() = false
        set(value) {
            await = value
        }

    fun await(): Keyword {
        await = true
        return this
    }

    fun awaitRender(): String {
        return "await ${render()}"
    }

    fun <M : Keyword, N : Keyword> applyMethods(call: (M) -> Unit, element: M, objectToRefer: N): Reference<N> {
        call(element)
        jsReturn += element
        return objectToRefer.refer()
    }
}