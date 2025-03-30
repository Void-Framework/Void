package io.void.js

import io.void.js.keywords.Keyword

class JavaScript(val runBeforeLoad: Boolean = false, val code: JavaScript.() -> Unit) {

    val children = mutableListOf<Keyword>()

    init {
        this.apply(code)
    }

    fun render(): String {
        if (children.isEmpty()) {
            return ""
        }

        return children.joinToString("\n") { it.render() }
    }
}