package main.html.element.content

import main.html.attributes.Attribute
import main.html.element.Element

class Div(vararg attributes: Attribute,function: Element.() -> Unit) : Element("div")

inline fun <reified T : Element> Element.element(block: T.() -> Unit): T {
    val instance = T::class.java.getDeclaredConstructor().newInstance()
    children.add(instance.apply(block))
    return instance
}