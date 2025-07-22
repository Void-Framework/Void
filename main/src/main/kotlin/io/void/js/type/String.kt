package io.void.js.type

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.emptyJsValue

class JsString(baseString: JsValue<String>) : Keyword {

    override var jsReturn: String = baseString.toJs()

    override fun render(): String {
        return jsReturn
    }

    fun charAt(index: JsValue<Int>): String {
        jsReturn += ".charAt($index)"
        return ""
    }

    fun concat(strings: JsValue<String>): String {
        jsReturn += ".concat($strings)"
        return ""
    }

    fun includes(string: JsValue<String>, position: JsValue<Int>? = null): Boolean {
        jsReturn += ".includes($string${if (position != null) ", $position" else ""})"
        return true
    }

    fun indexOf(string: JsValue<String>, position: JsValue<Int>? = null): Int {
        jsReturn += ".indexOf($string${if (position != null) ", $position" else ""})"
        return 0
    }

    fun lastIndexOf(string: JsValue<String>, position: JsValue<Int>? = null): Int {
        jsReturn += ".lastIndexOf($string${if (position != null) ", $position" else ""})"
        return 0
    }

    fun slice(begin: JsValue<Int>, end: JsValue<Int>? = null): String {
        jsReturn += ".slice($begin${if (end != null) ", $end" else ""})"
        return ""
    }

    fun substring(start: JsValue<Int>, end: JsValue<Int>? = null): String {
        jsReturn += ".substring($start${if (end != null) ", $end" else ""})"
        return ""
    }

    fun replace(searchValue: JsValue<String>, replaceValue: JsValue<String>): String {
        jsReturn += ".replace($searchValue, $replaceValue)"
        return ""
    }

    fun replaceAll(searchValue: JsValue<String>, replaceValue: JsValue<String>): String {
        jsReturn += ".replaceAll($searchValue, $replaceValue)"
        return ""
    }

    fun split(separator: JsValue<String>, limit: JsValue<Int>? = null, call: (JsList<String>) -> Unit): Reference<JsString> {
        jsReturn += ".split($separator${if (limit != null) ", $limit" else ""})"
        return applyMethods(call, JsList(emptyJsValue() as JsValue<String>), this)
    }

    fun toLowerCase(): String {
        jsReturn += ".toLowerCase()"
        return ""
    }

    fun toUpperCase(): String {
        jsReturn += ".toUpperCase()"
        return ""
    }

    fun trim(): String {
        jsReturn += ".trim()"
        return ""
    }

    fun trimStart(): String {
        jsReturn += ".trimStart()"
        return ""
    }

    fun trimEnd(): String {
        jsReturn += ".trimEnd()"
        return ""
    }

    fun startsWith(searchString: JsValue<String>, position: JsValue<Int>? = null): Boolean {
        jsReturn += ".startsWith($searchString${if (position != null) ", $position" else ""})"
        return true
    }

    fun endsWith(searchString: JsValue<String>, length: JsValue<Int>? = null): Boolean {
        jsReturn += ".endsWith($searchString${if (length != null) ", $length" else ""})"
        return true
    }

    fun repeat(count: JsValue<Int>): String {
        jsReturn += ".repeat($count)"
        return ""
    }

    fun padStart(targetLength: JsValue<Int>, padString: JsValue<String>? = null): String {
        jsReturn += ".padStart($targetLength${if (padString != null) ", $padString" else ""})"
        return ""
    }

    fun padEnd(targetLength: JsValue<Int>, padString: JsValue<String>? = null): String {
        jsReturn += ".padEnd($targetLength${if (padString != null) ", $padString" else ""})"
        return ""
    }

    fun match(regexp: JsValue<String>, call: (JsList<String>) -> Unit): Reference<JsString> {
        jsReturn += ".match($regexp)"
        return applyMethods(call, JsList(emptyJsValue() as JsValue<String>), this)
    }

    fun search(regexp: JsValue<String>): Int {
        jsReturn += ".search($regexp)"
        return -1
    }

    fun localeCompare(compareString: JsValue<String>, locales: JsValue<String>? = null, options: JsValue<String>? = null): Int {
        jsReturn += ".localeCompare($compareString${if (locales != null) ", $locales" else ""}${if (options != null) ", $options" else ""})"
        return 0
    }

    val length: Int
        get() {
            jsReturn += ".length"
            return 0
        }
}

fun JavaScript.charAt(baseString: JsValue<String>, index: JsValue<Int>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.charAt(index)
}

fun JavaScript.concat(baseString: JsValue<String>, strings: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.concat(strings)
}

fun JavaScript.includes(baseString: JsValue<String>, string: JsValue<String>, position: JsValue<Int>? = null): Boolean {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.includes(string, position)
}

fun JavaScript.indexOf(baseString: JsValue<String>, string: JsValue<String>, position: JsValue<Int>? = null): Int {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.indexOf(string, position)
}

fun JavaScript.lastIndexOf(baseString: JsValue<String>, string: JsValue<String>, position: JsValue<Int>? = null): Int {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.lastIndexOf(string, position)
}

fun JavaScript.slice(baseString: JsValue<String>, begin: JsValue<Int>, end: JsValue<Int>? = null): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.slice(begin, end)
}

fun JavaScript.substring(baseString: JsValue<String>, start: JsValue<Int>, end: JsValue<Int>? = null): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.substring(start, end)
}

fun JavaScript.replace(baseString: JsValue<String>, searchValue: JsValue<String>, replaceValue: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.replace(searchValue, replaceValue)
}

fun JavaScript.replaceAll(baseString: JsValue<String>, searchValue: JsValue<String>, replaceValue: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.replaceAll(searchValue, replaceValue)
}

fun JavaScript.split(baseString: JsValue<String>, separator: JsValue<String>, limit: JsValue<Int>? = null, call: (JsList<String>) -> Unit): Reference<JsString> {
    val jsString = JsString(baseString)
    val result = jsString.split(separator, limit, call)
    children.add(jsString)
    return result
}

fun JavaScript.toLowerCase(baseString: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.toLowerCase()
}

fun JavaScript.toUpperCase(baseString: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.toUpperCase()
}

fun JavaScript.trim(baseString: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.trim()
}

fun JavaScript.trimStart(baseString: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.trimStart()
}

fun JavaScript.trimEnd(baseString: JsValue<String>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.trimEnd()
}

fun JavaScript.startsWith(baseString: JsValue<String>, searchString: JsValue<String>, position: JsValue<Int>? = null): Boolean {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.startsWith(searchString, position)
}

fun JavaScript.endsWith(baseString: JsValue<String>, searchString: JsValue<String>, length: JsValue<Int>? = null): Boolean {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.endsWith(searchString, length)
}

fun JavaScript.repeat(baseString: JsValue<String>, count: JsValue<Int>): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.repeat(count)
}

fun JavaScript.padStart(baseString: JsValue<String>, targetLength: JsValue<Int>, padString: JsValue<String>? = null): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.padStart(targetLength, padString)
}

fun JavaScript.padEnd(baseString: JsValue<String>, targetLength: JsValue<Int>, padString: JsValue<String>? = null): String {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.padEnd(targetLength, padString)
}

fun JavaScript.match(baseString: JsValue<String>, regexp: JsValue<String>, call: (JsList<String>) -> Unit): Reference<JsString> {
    val jsString = JsString(baseString)
    val result = jsString.match(regexp, call)
    children.add(jsString)
    return result
}

fun JavaScript.search(baseString: JsValue<String>, regexp: JsValue<String>): Int {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.search(regexp)
}

fun JavaScript.localeCompare(baseString: JsValue<String>, compareString: JsValue<String>, locales: JsValue<String>? = null, options: JsValue<String>? = null): Int {
    val jsString = JsString(baseString)
    children.add(jsString)
    return jsString.localeCompare(compareString, locales, options)
}