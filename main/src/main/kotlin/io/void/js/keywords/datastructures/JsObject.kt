package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Keyword

data class JsObject(val values: Map<String, Any?>): JsDatastructure {

    override var jsReturn: String = ""
    private var inside = StringBuilder("")

    init {
        values.forEach { (name, key) ->
            inside.append("$name: $key,")
        }
        if (values.isNotEmpty()) {
            inside.setLength(inside.length - 1)
        }
    }

    override fun render(): String {
        return jsReturn
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "{$inside}"
        return this
    }

    inner class Actions {
        fun getValue(key: String): JsObject {
            jsReturn += ".$key"
            return this@JsObject
        }
        fun setValue(key: String, value: Any?): JsObject {
            jsReturn += ".$key = $value"
            return this@JsObject
        }
    }
}

data class ObjectsMethods(private val objectName: String): Keyword {
    override var jsReturn: String = "Object"

    override fun render(): String {
        return jsReturn
    }

    fun delete(key: String): ObjectsMethods {
        jsReturn = "delete $objectName.$key"
        return this
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

fun JavaScript.jsObject(values: Map<String, Any?>): JsObject {
    val JsObject = JsObject(
        values = values
    ).initialize()
    children.add(JsObject)
    return JsObject as JsObject
}

fun JavaScript.objectMethod(objectName: String): ObjectsMethods {
    val methods = ObjectsMethods(
        objectName = objectName
    )
    children.add(methods)
    return methods
}

