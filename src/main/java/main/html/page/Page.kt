package main.html.page

import main.html.element.Element

abstract class Page(val target: String) {

    abstract var content: Element
}