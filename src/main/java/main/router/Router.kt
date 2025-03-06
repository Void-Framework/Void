package main.router

import main.html.exceptions.ExceptionPage
import main.api.ApiPage
import main.html.page.Page
import main.java.main.DTO.RequestDTO
import main.java.main.DTO.ResponseDTO
import main.java.main.HTTP.Builder.HTTPBuilder
import main.router.exceptions.RouteNoTargetException
import main.router.exceptions.RouteTargetUsedException
import java.net.Socket

class Router {

    companion object {
        val routes = mutableMapOf<String, Page>()

        //Add a function to add routes without finding the annotations
        fun addRoute(route: Page) {

            if (routes.containsKey(route.target)) {
                throw RouteTargetUsedException(route.target)
            } else {
                if (route.target.startsWith("/")) {
                    routes[route.target] = route
                } else {
                    throw RouteNoTargetException(route.target)
                }
            }
        }

    }



    fun route(requestDTO: RequestDTO, client: Socket) {
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            if (routes[target] is ApiPage) {
                (routes[target] as ApiPage).serverGetter()
                HTTPBuilder().build(
                    ResponseDTO(
                        200,
                        "All is well",
                        mapOf(
                            "Content-Type" to "text/html",
                            "Upgrade" to "websocket",
                            "Connection" to "Upgrade"
                        ),
                        "<html><body>${routes[target]!!.content.render()}</body></html>"
                    ),
                    client.getOutputStream()
                )
            } else {
                HTTPBuilder().build(
                    ResponseDTO(
                        200,
                        "All is well",
                        mapOf(
                            "Content-Type" to "text/html",
                            "Upgrade" to "websocket",
                            "Connection" to "Upgrade"
                        ),
                        "<html><body>${routes[target]!!.content.render()}</body></html>"
                    ),
                    client.getOutputStream()
                )
            }
        } else {
            HTTPBuilder().build(
                ResponseDTO(404,
                "Not Found",
                mapOf(
                    "Content-Type" to "text/html",
                    "Upgrade" to "websocket",
                    "Connection" to "Upgrade"
                ),
                "<html><body><h1>No Route Found!</h1></body></html>"),
                client.getOutputStream())
        }
    }

    fun error(client: Socket, e: Exception) {
        HTTPBuilder().build(
            ResponseDTO(500,
                "Server Error",
                mapOf(
                    Pair("Content-Type", "text/html"),
                    Pair("Connection", "close")
                ),
                ExceptionPage(e).page),
            client.getOutputStream())
    }
}