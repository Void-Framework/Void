package io.jadiefication

import io.jadiefication.middleware.LogMiddleware
import io.jadiefication.routes.home.HomeRoute
import io.jadiefication.routes.setter.SetterRoute
import io.jadiefication.routes.user.UserRoute
import io.void.router.Router
import io.void.server.Server

val router = Router(listOf(LogMiddleware())).addRoutes(listOf(HomeRoute(), SetterRoute(), UserRoute()))

fun main() {
    val server = Server(
        router = router
    )

    server.startHTTPServer(
        port = 8080,
        routeToHTTPS = false
    )

}
