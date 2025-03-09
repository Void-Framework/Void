package io.jadiefication

import io.jadiefication.routes.home.HomeRoute
import io.jadiefication.routes.setter.SetterRoute
import io.void.router.Router
import io.void.server.Server

val router = Router().addRoutes(listOf(HomeRoute(), SetterRoute()))

fun main() {
    val server = Server(router = router)

    server.startServer(8080)

}
