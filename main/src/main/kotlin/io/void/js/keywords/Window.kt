package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable
import java.net.URL

class Window(val window: Variable<Window>? = null): Keyword {

    override var jsReturn: String = window?.name ?: "window"

    override fun render(): String {
        return jsReturn
    }

    fun alert(message: String?): Void {
        jsReturn += ".alert(${
            if (message != null) {
                if (TemplateString.isTemplateString(message)) {
                    TemplateString.turnToTemplateString(message)
                } else {
                    "\"$message\""
                }
            } else {
                "\"\""
            }
        })"
        return Void()
    }
    fun open(url: URL? = null, name: String? = null, specs: List<String>? = null): Window {
        jsReturn += ".open(\"${url?.toString() ?: ""}\", \"${name ?: ""}\", \"${specs?.joinToString(",") ?: ""}\")"
        return Window()
    }
    fun confirm(message: String): Void {
        jsReturn += ".confirm(${if (TemplateString.isTemplateString(message)) {
            TemplateString.turnToTemplateString(message)
        } else {
            "\"$message\""
        }
        })"
        return Void()
    }
    fun history(): History {
        jsReturn += ".history"
        return History()
    }
    fun href(url: URL?): Void {
        jsReturn += ".location.href${
            if (url != null) {
                if (TemplateString.isTemplateString(url.toString())) {
                    TemplateString.turnToTemplateString(url.toString())
                } else {
                    "\"$url\""
                }
            } else {
                "\"\""
            }
        }"
        return Void()
    }
    fun prompt(message: String? = null, defaultText: String? = null): Void {
        jsReturn += ".prompt(${
            if (message != null) {
                if (TemplateString.isTemplateString(message)) {
                    TemplateString.turnToTemplateString(message)
                } else {
                    "\"$message\""
                }
            } else {
                "\"\""
            }
        }, ${
            if (message != null) {
                if (TemplateString.isTemplateString(message)) {
                    TemplateString.turnToTemplateString(message)
                } else {
                    "\"$message\""
                }
            } else {
                "\"\""
            }
        })"
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