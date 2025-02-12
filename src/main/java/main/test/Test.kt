package main.java.main.Test

import main.html.attributes.AtrributeTypes
import main.html.attributes.attribute
import main.html.element.content.Div
import main.html.element.content.H1
import main.html.element.content.element
import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute

val divAtt = attribute {
    name = "class"
    type = AtrributeTypes.STRING
}

fun main() {
    val server = Server("main.test.routes")

    Router.addRoute(HomeRoute())

    server.startServer(8080)

    val random = Div(divAtt) {
        element<H1> { textContent = "Hello" }
    }
}
