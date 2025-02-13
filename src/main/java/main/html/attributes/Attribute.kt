package main.html.attributes

class Attribute {

    var name: String = ""
    var type: AtrributeTypes = AtrributeTypes.NULL
    var value: String = ""
}

fun attribute(func: Attribute.() -> Unit): Attribute {
    val attribute = Attribute()
    attribute.func()
    return attribute
}