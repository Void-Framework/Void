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
import java.util.concurrent.ConcurrentHashMap

class Router {

    private val routes: ConcurrentHashMap<String, Page> = ConcurrentHashMap()

    //Add a function to add routes without finding the annotations
    fun addRoute(route: Page): Router {

        if (routes.containsKey(route.target)) {
            throw RouteTargetUsedException(route.target)
        } else {
            if (route.target.startsWith("/")) {
                routes[route.target] = route
            } else {
                throw RouteNoTargetException(route.target)
            }
        }
        return this
    }

    fun addRoutes(routes: List<Page>): Router {
        routes.forEach {
            addRoute(it)
        }
        return this
    }

    fun route(requestDTO: RequestDTO, client: Socket) {
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            if (routes[target] is ApiPage) {
                HTTPBuilder().build(
                    (routes[target] as ApiPage).serverGetter(request = requestDTO),
                    client.getOutputStream()
                )
            } else {
                HTTPBuilder().build(
                    response = ResponseDTO(
                        status = 200,
                        statusText = "All is well",
                        headers = mapOf(
                            "Content-Type" to "text/html",
                            "Upgrade" to "websocket",
                            "Connection" to "Upgrade"
                        ),
                        body = "<html><body>${routes[target]!!.content!!.render()}</body></html>"
                    ),
                    outputStream = client.getOutputStream()
                )
            }
        } else {
            HTTPBuilder().build(
                response = ResponseDTO(status = 404,
                statusText = "Not Found",
                headers = mapOf(
                    "Content-Type" to "text/html",
                    "Upgrade" to "websocket",
                    "Connection" to "Upgrade"
                ),
                body = "<html><body><h1>No Route Found!</h1></body></html>"),
                outputStream = client.getOutputStream())
        }
    }

    fun error(client: Socket, e: Exception) {
        HTTPBuilder().build(
            response = ResponseDTO(status = 500,
                statusText = "Server Error",
                headers = mapOf(
                    "Content-Type" to "text/html",
                    "Connection" to "close"
                ),
                body = ExceptionPage(e).page),
            outputStream = client.getOutputStream())
    }
}