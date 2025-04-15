package io.void.js.keywords

interface Keyword {

    var jsReturn: String
    fun render(): String
    fun await(): Keyword {
        jsReturn = "await $jsReturn"
        return this
    }
}