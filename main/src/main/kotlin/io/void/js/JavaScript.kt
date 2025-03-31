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

        val js = children.mapIndexed { index, keyword ->
            val rendered = keyword.render()
            if (!rendered.endsWith(";")) {
                // Check if next line starts with a dot or is a function/class declaration
                val nextLine = if (index < children.size - 1) {
                    children[index + 1].render()
                } else null

                if (nextLine?.startsWith(".") == true ||
                    rendered.contains("function") ||
                    rendered.contains("class")) {
                    rendered
                } else {
                    "$rendered;"
                }
            } else rendered
        }.joinToString("\n")
        println(js)
        return js
    }
}