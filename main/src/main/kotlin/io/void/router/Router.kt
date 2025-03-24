package io.void.router

import io.void.cache.Cache
import io.void.cache.Cacheable
import io.void.cache.Processor
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.html.exceptions.ExceptionPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.http.builder.HTTPBuilder
import io.void.router.exceptions.RouteNoTargetException
import io.void.router.exceptions.RouteTargetUsedException
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class Router {

    private val routes: ConcurrentHashMap<String, Page<*>> = ConcurrentHashMap()
    private val builder = HTTPBuilder()

    //Add a function to add routes without finding the annotations
    fun addRoute(route: Page<*>): Router {
        Processor.annotationProcessor(listOf(route), this)
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

    fun addRoutes(routes: List<Page<*>>): Router {
        routes.forEach {
            addRoute(it)
        }
        return this
    }

    fun route(requestDTO: RequestDTO, client: Socket) {
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            val page = routes[target]
            page!!.request = requestDTO
            if (page.content() is ContentType.Response) {
                builder.build((page.content() as ContentType.Response).response, client.getOutputStream())
            } else {
                val response = if (Cache.singleton.cache.containsKey(target)) {
                    Cache.singleton.cache[target]!!
                } else {
                    ResponseDTO(
                        status = 200,
                        statusText = "All is well",
                        headers = mutableMapOf(
                            "Content-Type" to "text/html",
                        ),
                        body = "<html><body>${(page.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
                    )
                }

                builder.build(
                    response = response,
                    outputStream = client.getOutputStream()
                )
            }
        } else {
            builder.build(
                response = ResponseDTO(status = 404,
                statusText = "Not Found",
                headers = mutableMapOf(
                    "Content-Type" to "text/html",
                ),
                body = "<html><body><h1>No Route Found!</h1></body></html>"),
                outputStream = client.getOutputStream())
        }
    }

    fun error(client: Socket, e: Exception) {
        builder.build(
            response = ResponseDTO(status = 500,
                statusText = "Server Error",
                headers = mutableMapOf(
                    "Content-Type" to "text/html",
                    "Connection" to "close"
                ),
                body = ExceptionPage(e).page),
            outputStream = client.getOutputStream())
    }
}