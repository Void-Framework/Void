package io.void.js.keywords.async

import io.void.js.JavaScript
import io.void.js.keywords.Keyword
import io.void.js.keywords.datastructures.Void
import java.net.URL

class Window: Keyword {

    override var jsReturn: String = "window"

    override fun render(): String {
        return jsReturn
    }

    fun alert(message: String?): Void {
        jsReturn += ".alert(\"${message ?: ""}\")"
        return Void()
    }
    fun open(url: URL? = null, name: String? = null, specs: List<String>? = null): Window {
        jsReturn += ".open(\"${url?.toString() ?: ""}\", \"${name ?: ""}\", \"${specs?.joinToString(",") ?: ""}\")"
        return Window()
    }
    fun confirm(message: String): Void {
        jsReturn += ".confirm(\"$message\")"
        return Void()
    }
    fun history(): History {
        jsReturn += ".history"
        return History()
    }
    fun href(url: URL?): Void {
        jsReturn += ".location.href${if (url != null) {
            " = \"$url\""
        } else {
            ""
        }
        }"
        return Void()
    }
    fun prompt(message: String? = null, defaultText: String? = null): Void {
        jsReturn += ".prompt(\"${message ?: ""}\", \"${defaultText ?: ""}\")"
        return Void()
    }
    fun scrollBy(x: Int, y: Int): Void {
        jsReturn += ".scrollBy($x, $y)"
        return Void()
    }
    fun scrollTo(x: Int, y: Int): Void {
        jsReturn += ".scrollTo($x, $y)"
        return Void()
    }

    inner class History {
        fun back(): Void {
            jsReturn += ".back()"
            return Void()
        }
        fun forward(): Void {
            jsReturn += ".forward()"
            return Void()
        }
        fun go(number: Int): Void {
            jsReturn += ".go($number)"
            return Void()
        }
    }
}

fun JavaScript.window(): Window {
    val window = Window()
    children.add(window)
    return window
}