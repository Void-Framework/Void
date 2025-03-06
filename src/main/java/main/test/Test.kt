package main.java.main.Test

import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute
import main.test.routes.setter.SetterRoute

fun main() {
    val server = Server()

    Router.addRoute(HomeRoute())
    Router.addRoute(SetterRoute())

    server.startServer(8080)

}
