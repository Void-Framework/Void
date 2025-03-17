package io.void.js

import com.sun.org.apache.xerces.internal.dom.NodeImpl
import io.void.html.Element
import io.void.html.Fragment
import kotlinx.html.dom.document
import org.w3c.dom.Node

interface JSHandler {

    val document
        get() = document {
            this.normalizeDocument()
        }

    fun addElement(element: Element): Node? {
        if (element is Fragment) {
            val children = element.children
            if (children != null) {
                val parent = document.createElement("div")
                children.forEach {
                    //parent.addChild()
                }
            } else {
                return document.createTextNode(element.text)
            }
        }
        val parent = document.createElement(element.name)
        if (element.children?.isEmpty() == true) {
            return parent
        } else {
            element.children?.forEach {
                addElement(it)?.let { it1 -> parent.addChild(it1) }
            }
            return parent
        }
    }

    fun addElements(element: MutableList<Element>): List<Node> {
        val elements = mutableListOf<Node>()
        element.forEach {
            val addedElement = addElement(it)
            if (addedElement != null) {
                elements.add(addedElement)
            }
        }
        return elements
    }
}

fun Node.addChild(child: Node): Node {
    val children = childNodes

    return child
}