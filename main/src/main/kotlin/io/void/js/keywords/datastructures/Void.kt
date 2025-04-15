package io.void.js.keywords.datastructures

class Void: JsDatastructure {
    override var jsReturn: String = ""

    override fun render(): String {
        return jsReturn
    }
    override fun initialize(): JsDatastructure {
        return this
    }
}