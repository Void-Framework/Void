package io.void.html.page

import io.void.html.element.Element

abstract class Page(open val target: String) {

    abstract var content: Element?
}