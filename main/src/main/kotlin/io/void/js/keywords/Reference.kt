package io.void.js.keywords

data class Reference<T: Keyword>(
    private val keywordProvider: () -> T
): Keyword {

    override var jsReturn: String = ""
        get() = keywordProvider().render()

    override fun render(): String {
        return keywordProvider().render()
    }

    override fun toString(): String {
        return render()
    }
}


fun <T: Keyword> T.refer(): Reference<T> {
    return Reference { this }
}