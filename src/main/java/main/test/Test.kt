package main.java.main.Test

import main.html.attributes.AtrributeTypes
import main.html.attributes.attribute
import main.html.element.content.*
import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute

fun main() {
    val server = Server()

    Router.addRoute(HomeRoute())

    server.startServer(8080)

}
