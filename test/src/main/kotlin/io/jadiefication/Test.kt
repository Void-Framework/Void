package io.jadiefication

import io.jadiefication.routes.home.homeRoute
import io.jadiefication.routes.setter.setterRoute
import io.jadiefication.routes.user.userRoute
import io.void.middleware.middleware
import io.void.router.Router
import io.void.router.router
import io.void.server.Server
import io.void.server.server

fun main() {
    val server =
        server {
            router =
                router {
                    +middleware {
                        after = { result ->
                            result.fold(
                                onSuccess = { println(it) },
                                onFailure = { println(it) },
                            )
                            null
                        }
                        +homeRoute
                        +setterRoute
                        +userRoute
                    }
                    port = 8080
                    routeToHTTPS = false
                }
        }
}
