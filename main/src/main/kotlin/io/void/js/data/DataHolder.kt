package io.void.js.data

import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.js.JavaScript
import io.void.js.keywords.*
import io.void.js.Function
import io.void.js.FunctionRunner
import io.void.js.Js
import io.void.js.keywords.variable.Const
import java.util.UUID

class DataHolder(val js: JavaScript): Keyword {

    override var jsReturn: String = ""

    fun initialize(baseValue: Any?): DataHolder {
        jsReturn = "ref($baseValue)"
        return this
    }

    override fun render(): String {
        return jsReturn
    }

    fun read(): Any {
        jsReturn += ".read()"
        return ""
    }
    fun write(value: JsValue<*>) {
        jsReturn += ".write($value)"
    }
}

fun Element.get(dataHolder: Const<DataHolder>): Fractal {
    val uuid = UUID.randomUUID().toString()
    this.attributes[AttributeNames.V_DATAHOLD] = uuid
    dataHolder.value!!.js.children.addFirst(InlineCall("bindText(document.querySelector('[v-datahold=\"$uuid\"]'), ${dataHolder.name})"))
    return Fractal(text = "")
}