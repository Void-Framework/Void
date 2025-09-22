package io.jadiefication

import io.jadiefication.middleware.LogMiddleware
import io.jadiefication.routes.home.HomeRoute
import io.jadiefication.routes.setter.SetterRoute
import io.jadiefication.routes.user.UserRoute
import io.void.router.Router
import io.void.router.router
import io.void.server.Server
import io.void.server.server

fun main() {
    val server =
        server {
            router = router {
                +LogMiddleware()
                +HomeRoute()
                +SetterRoute()
                +UserRoute()
            }
            port = 8080
            routeToHTTPS = false
        }
}
