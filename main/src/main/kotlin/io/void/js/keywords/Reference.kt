package io.void.js.keywords

data class Reference<T: Keyword>(
    val call: T
): Keyword, JsValue<T> {

    override var jsReturn: String = call.render()
    override fun render(): String {
        return jsReturn
    }
    override fun toJs(): String {
        return render()
    }
}

fun <T: Keyword> T.refer(): Reference<T> {
    return Reference(this)
}