package main.html.element.content

import main.html.element.Element

class H1(text: String): Element("h1") {
    var textContent: String = text
}