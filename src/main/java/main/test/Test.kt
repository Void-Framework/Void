package main.java.main.Test

import main.java.main.Server.Server
import main.router.Router
import main.test.routes.home.HomeRoute
import main.test.routes.setter.SetterRoute

val router = Router().addRoute(HomeRoute())

fun main() {
    val server = Server(router = router)

    server.startServer(8080)

}
