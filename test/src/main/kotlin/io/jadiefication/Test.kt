package io.jadiefication

import io.jadiefication.routes.home.homeRoute
import io.jadiefication.routes.home.ktsHelloRoute
import io.jadiefication.routes.setter.setterRoute
import io.jadiefication.routes.user.userRoute
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.middleware.relayAfter
import io.void.server.simpleServer

fun main() {
    /*val server =
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
                    }
                    +homeRoute
                    +setterRoute
                    +userRoute
                    +ktsHelloRoute
                    on("/hello")
                        .get { req ->
                            buildResponse {
                                status = 200
                                statusText = "OK"
                                headers {
                                    put("Content-Type", "text/plain")
                                }
                                body = "Hello, ${req.headers["User-Agent"] ?: "stranger"}!"
                            }
                        }
                        .post { req ->
                            buildResponse {
                                status = 201
                                statusText = "Created"
                                headers {
                                    put("Content-Type", "application/json")
                                }
                                body = """{ "message": "You posted: ${req.body}" }"""
                            }
                        }
                    port = 8080
                    routeToHTTPS = false
                }
        }*/

    val server = simpleServer {
        +relayAfter { result ->
            result.fold(
                onSuccess = { println(it) },
                onFailure = { println(it) },
            )
            null
        }
        +homeRoute
        +setterRoute
        +userRoute
        +ktsHelloRoute
        on("/hello") GET { req -> buildResponse {
            status = 200
            statusText = "OK"
            headers {
                put("Content-Type", "text/plain")
            }
            body = "Hello, ${req.headers["User-Agent"] ?: "stranger"}!"
        } }
        on("/hello") POST { req -> buildResponse {
                    status = 201
                    statusText = "Created"
                    headers {
                        put("Content-Type", "application/json")
                    }
                    body = """{ "message": "You posted: ${req.body}" }"""
                }
            }
    }
}
