package io.void.js.keywords

abstract class Keyword(val name: String) {

    abstract fun render(): String
}