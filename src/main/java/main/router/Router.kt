package main.router

import main.html.exceptions.ExceptionPage
import main.html.page.Page
import main.java.main.DTO.RequestDTO
import main.java.main.DTO.ResponseDTO
import main.java.main.HTTP.Builder.HTTPBuilder
import main.router.exceptions.NotAnnotatedException
import main.router.exceptions.RouteNoTargetException
import main.router.exceptions.RouteTargetUsedException
import java.net.Socket
import kotlin.reflect.full.findAnnotation

class Router {

    companion object {
        val routes = mutableMapOf<String, Page>()

        //Add a function to add routes without finding the annotations
        fun addRoute(route: Page) {
            val clazz = route::class
            val annotation = clazz.findAnnotation<Route>()

            if (annotation != null) {
                if (routes.containsKey(annotation.target)) {
                    throw RouteTargetUsedException()
                } else {
                    if (annotation.target.startsWith("/")) {
                        routes[annotation.target] = route
                    } else {
                        throw RouteNoTargetException()
                    }
                }
            } else {
                throw NotAnnotatedException()
            }
        }

    }



    fun route(requestDTO: RequestDTO, client: Socket) {
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            HTTPBuilder().build(ResponseDTO(200,
                "All is well",
                mapOf(
                    Pair("Content-Type", "text/html"),
                    Pair("Upgrade", "websocket"),
                    Pair("Connection", "Upgrade")
                ),
                "<html><body>${routes[target]!!.content.render()}</body></html>"),
                client.getOutputStream())
        } else {
            HTTPBuilder().build(
                ResponseDTO(404,
                "Not Found",
                mapOf(
                    Pair("Content-Type", "text/html"),
                    Pair("Upgrade", "websocket"),
                    Pair("Connection", "Upgrade")
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