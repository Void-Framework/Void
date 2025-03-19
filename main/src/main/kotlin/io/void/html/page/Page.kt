package io.void.html.page

import io.void.html.Element
import io.void.ws.WSClient

abstract class Page(open val target: String) {

    abstract var content: Element?
    internal lateinit var client: WSClient
}