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

    fun getValue(key: String): Void {
        jsReturn += ".$key"
        return Void()
    }
    fun setValue(key: String, value: Any?): Void {
        jsReturn += ".$key = $value"
        return Void()
    }
}

data class ObjectsMethods(private val objectName: String, val js: JavaScript): Keyword {
    override var jsReturn: String = "Object"

    override fun render(): String {
        return jsReturn
    }

    fun delete(key: String): Void {
        jsReturn = "delete $objectName.$key"
        return Void()
    }
    fun keys(): JsList<String> {
        jsReturn += ".keys($objectName)"
        val list = JsList<String>(listOf())
        js.children.add(list)
        return list
    }
    fun values(): JsList<Any> {
        jsReturn += ".values($objectName)"
        val list = JsList<Any>(listOf())
        js.children.add(list)
        return list
    }
    fun entries(): JsList<JsList<Any>> {
        jsReturn += ".entries($objectName)"
        val list = JsList<JsList<Any>>(listOf())
        js.children.add(list)
        return list
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
        objectName = objectName,
        js = this
    )
    children.add(methods)
    return methods
}

