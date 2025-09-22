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
            this.router = router {
                middleware.add(LogMiddleware())
                addRoutes(listOf(
                    HomeRoute(),
                    SetterRoute(),
                    UserRoute()
                ))
            }
            this.port = 8080
            this.routeToHTTPS = false
        }
}
