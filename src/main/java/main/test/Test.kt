package main.java.main.Test

import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute
import main.test.routes.setter.SetterRoute

val router = Router().addRoutes(listOf(HomeRoute(), SetterRoute()))

fun main() {
    val server = Server(router = router)

    server.startHTTPServer(8080)

}
