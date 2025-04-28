package io.void.js.data

import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.js.JavaScript
import io.void.js.keywords.*
import io.void.js.Function
import io.void.js.FunctionRunner
import java.util.UUID

class DataHolder: Keyword {

    override var jsReturn: String = ""

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