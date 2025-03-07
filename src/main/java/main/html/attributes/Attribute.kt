package main.html.attributes

import main.html.attributes.exception.UnsupportedTypeException
import java.net.URL

class Attribute {

    var name: AttributeNames = AttributeNames.EMPTY
    var type: AttributeTypes = AttributeTypes.NULL
    var value: Any? = null

    fun isCorrectValue(): Boolean {
        when (value) {
            null -> return type == AttributeTypes.NULL
            is String -> return type == AttributeTypes.STRING
            is Boolean -> return type == AttributeTypes.BOOLEAN
            is Int -> return type == AttributeTypes.INTEGER
            is Number -> return type == AttributeTypes.NUMBER
            is URL -> return type == AttributeTypes.URL
            else -> throw UnsupportedTypeException()
        }
    }
}

fun attribute(func: Attribute.() -> Unit): Attribute {
    val attribute = Attribute()
    attribute.func()
    return attribute
}