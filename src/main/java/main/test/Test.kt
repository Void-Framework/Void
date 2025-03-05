package main.java.main.Test

import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute

fun main() {
    val server = Server()

    Router.addRoute(HomeRoute())

    server.startServer(8080)

}
