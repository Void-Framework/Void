package io.void.js.keywords.string

class TemplateString {

    companion object {
        fun isTemplateString(text: String): Boolean {
            return text.contains("(?<!\\\\)\\\$\\{[^}]".toRegex())
        }
        fun turnToTemplateString(text: String): String {
            return "`$text`"
        }
    }
}