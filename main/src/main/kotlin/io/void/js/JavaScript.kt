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

        val js = children.joinToString("\n") {
            val rendered = it.render()
            if (!rendered.endsWith(";") && !(rendered.contains("function") || rendered.contains("class"))) "$rendered;" else rendered
        }
        println(js)
        return js
    }
}