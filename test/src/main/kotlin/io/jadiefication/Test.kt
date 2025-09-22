package io.jadiefication

import io.jadiefication.middleware.LogMiddleware
import io.jadiefication.routes.home.homeRoute
import io.jadiefication.routes.setter.setterRoute
import io.jadiefication.routes.user.userRoute
import io.void.router.Router
import io.void.router.router
import io.void.server.Server
import io.void.server.server

fun main() {
    val server =
        server {
            router = router {
                +LogMiddleware()
                +homeRoute
                +setterRoute
                +userRoute
            }
            port = 8080
            routeToHTTPS = false
        }
}
