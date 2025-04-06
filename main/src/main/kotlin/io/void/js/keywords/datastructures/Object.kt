package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class Object(val values: Map<String, Any?>): Keyword {

    override var jsReturn: String = ""
    private var inside = ""

    init {
        values.forEach { (name, key) ->
            inside += "$name: $key,"
        }
        if (values.isNotEmpty()) {
            inside.replaceAfterLast(",", "")
        }
    }

    override fun render(): String {
        return jsReturn
    }

    fun initialize(): Object {
        jsReturn = "{$inside}"
        return this
    }

    inner class Actions {
        fun getValue(key: String) {
            jsReturn += ".$key"
        }
        fun setValue(key: String, value: Any?) {
            jsReturn += ".$key = $value"
        }
    }
}

data class ObjectsMethods(private val objectName: String): Keyword {
    override var jsReturn: String = "Object"

    override fun render(): String {
        return jsReturn
    }

    fun delete(key: String) {
        jsReturn = "delete $objectName.$key"
    }
    fun keys(): JsList<String> {
        jsReturn += ".keys($objectName)"
        return JsList(listOf())
    }
    fun values(): JsList<Any> {
        jsReturn += ".values($objectName)"
        return JsList(listOf())
    }
    fun entries(): JsList<JsList<Any>> {
        jsReturn += ".entries($objectName)"
        return JsList(listOf(JsList(listOf())))
    }
}

fun JavaScript.jsObject(values: Map<String, Any?>): Object {
    val Object = Object(
        values = values
    )
    children.add(Object)
    return Object
}

fun JavaScript.objectMethod(objectName: String): ObjectsMethods {
    val methods = ObjectsMethods(
        objectName = objectName
    )
    children.add(methods)
    return methods
}

