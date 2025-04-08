package io.void.js.keywords

import io.void.js.JavaScript
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.Void
import io.void.js.keywords.string.TemplateString

class Console: Keyword {

    override var jsReturn: String = "console"

    override fun render(): String {
        return jsReturn
    }
    fun error(message: String): Void {
        jsReturn += ".error(${if (TemplateString.isTemplateString(message)) {
            TemplateString.turnToTemplateString(message)
        } else {
            "\"$message\""
        }
        })"
        return Void()
    }
    fun info(message: String): Void {
        jsReturn += ".info(${if (TemplateString.isTemplateString(message)) {
            TemplateString.turnToTemplateString(message)
        } else {
            "\"$message\""
        }
        })"
        return Void()
    }
    fun log(message: String): Void {
        jsReturn += ".log(${if (TemplateString.isTemplateString(message)) {
            TemplateString.turnToTemplateString(message)
        } else {
            "\"$message\""
        }
        })"
        return Void()
    }
    fun table(list: JsList<*>): Void {
        jsReturn += ".table(${list.render()})"
        return Void()
    }
    fun warn(message: String): Void {
        jsReturn += ".warn(${if (TemplateString.isTemplateString(message)) {
            TemplateString.turnToTemplateString(message)
        } else {
            "\"$message\""
        }
        })"
        return Void()
    }
}

fun JavaScript.console(): Console {
    val console = Console()
    children.add(console)
    return console
}