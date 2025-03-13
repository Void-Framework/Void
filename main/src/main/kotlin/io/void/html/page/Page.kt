package io.void.html.page

import io.void.html.Element

abstract class Page(open val target: String) {

    abstract var content: Element?
}