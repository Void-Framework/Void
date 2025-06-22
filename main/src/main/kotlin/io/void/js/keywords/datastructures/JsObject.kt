package io.void.js.keywords.datastructures

import io.void.js.JavaScript
import io.void.js.keywords.Call
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.asJsValue
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.refer

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
    fun emptyObject(): JsObject {
        jsReturn = "{}"
        return this
    }

    override fun initialize(): JsDatastructure {
        jsReturn = "{$inside}"
        return this
    }

    fun value(key: JsValue<*>, call: (Any?) -> Unit): Reference<JsObject> {
        jsReturn += ".$key"
        return applyMethods(call, null, this)
    }
    fun value(key: String, value: JsValue<*>?): Reference<JsObject> {
        jsReturn += ".$key = $value"
        return this.refer()
    }
}

data class ObjectsMethods(private val objectName: JsValue<JsObject>): Keyword {
    override var jsReturn: String = "Object"

    override fun render(): String {
        return jsReturn
    }

    fun delete(key: String): Reference<ObjectsMethods> {
        jsReturn = "delete $objectName.$key"
        return this.refer()
    }
    fun keys(call: (JsList<String>) -> Unit): Reference<ObjectsMethods> {
        jsReturn += ".keys($objectName)"
        return applyMethods(call, JsList(emptyJsValue() as JsValue<String>), this)
    }
    fun values(call: (JsList<Any>) -> Unit): Reference<ObjectsMethods> {
        jsReturn += ".values($objectName)"
        return applyMethods(call, JsList(emptyJsValue() as JsValue<Any>), this)
    }
    fun entries(call: (JsList<JsList<Any>>) -> Unit): Reference<ObjectsMethods> {
        jsReturn += ".entries($objectName)"
        return applyMethods(call, JsList(JsList(emptyJsValue() as JsValue<Any>).asJsValue()), this)
    }
    fun assign(source: JsValue<JsObject>, call: (JsObject) -> Unit): Reference<ObjectsMethods> {
        jsReturn += ".assign($objectName, $source)"
        return applyMethods(call, JsObject(emptyMap()), this)
    }
}

fun JavaScript.jsObject(values: Map<String, JsValue<*>?>): JsObject {
    val JsObject = JsObject(
        values = values
    ).initialize()
    children.add(JsObject)
    return JsObject as JsObject
}

fun JavaScript.delete(objectName: JsValue<JsObject>, key: String) {
    val methods = ObjectsMethods(objectName)
    children.add(methods)
    methods.delete(key)
}
fun JavaScript.keys(objectName: JsValue<JsObject>, call: (JsList<String>) -> Unit): Reference<ObjectsMethods> {
    val methods = ObjectsMethods(objectName)
    children.add(methods)
    return methods.keys(call)
}
fun JavaScript.values(objectName: JsValue<JsObject>, call: (JsList<Any>) -> Unit): Reference<ObjectsMethods> {
    val methods = ObjectsMethods(objectName)
    children.add(methods)
    return methods.values(call)
}
fun JavaScript.entries(objectName: JsValue<JsObject>, call: (JsList<JsList<Any>>) -> Unit): Reference<ObjectsMethods> {
    val methods = ObjectsMethods(objectName)
    children.add(methods)
    return methods.entries(call)
}
fun JavaScript.assign(objectName: JsValue<JsObject>, source: JsValue<JsObject>, call: (JsObject) -> Unit): Reference<ObjectsMethods> {
    val methods = ObjectsMethods(objectName)
    children.add(methods)
    return methods.assign(source, call)
}
fun JavaScript.emptyObject(): JsObject {
    val set = JsObject(values = emptyMap()).emptyObject()
    children.add(set)
    return set as JsObject
}
fun Map<String, JsValue<*>>.asJsObject(): JsObject {
    val jsObject = JsObject(this)
    return jsObject
}

