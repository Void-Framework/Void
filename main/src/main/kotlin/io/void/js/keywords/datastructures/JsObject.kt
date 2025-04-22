package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.asJsValue
import io.void.js.keywords.emptyJsValue

data class JsObject(val values: Map<String, JsValue<*>?>): JsDatastructure {

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

    fun getValue(key: JsValue<*>): Void {
        jsReturn += ".$key"
        return Void()
    }
    fun setValue(key: String, value: JsValue<*>?): Void {
        jsReturn += ".$key = $value"
        return Void()
    }
}

data class ObjectsMethods(private val objectName: JsValue<*>): Keyword {
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
        val list = JsList(emptyJsValue() as JsValue<String>)
        return list
    }
    fun values(): JsList<Any> {
        jsReturn += ".values($objectName)"
        val list = JsList(emptyJsValue() as JsValue<Any>)
        return list
    }
    fun entries(): JsList<JsList<Any>> {
        jsReturn += ".entries($objectName)"
        @Suppress("UNCHECKED_CAST")
        val list = JsList(JsList(emptyJsValue() as JsValue<Any>).asJsValue())
        return list
    }
}

fun JavaScript.jsObject(values: Map<String, JsValue<*>?>): JsObject {
    val JsObject = JsObject(
        values = values
    ).initialize()
    children.add(JsObject)
    return JsObject as JsObject
}

fun JavaScript.objectMethod(objectName: JsValue<*>): ObjectsMethods {
    val methods = ObjectsMethods(
        objectName = objectName
    )
    children.add(methods)
    return methods
}

