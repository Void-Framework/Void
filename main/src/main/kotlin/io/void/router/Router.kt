package io.void.router

import io.void.cache.Cache
import io.void.cache.Processor
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.html.exceptions.ExceptionPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.http.builder.HTTPBuilder
import io.void.router.exceptions.RouteNoTargetException
import io.void.router.exceptions.RouteTargetUsedException
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class Router {

    private val routes: ConcurrentHashMap<String, Page<*>> = ConcurrentHashMap()
    private val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage<*>> = ConcurrentHashMap()
    private val builder = HTTPBuilder()

    //Add a function to add routes without finding the annotations
    fun addRoute(route: Page<*>): Router {
        Processor.annotationProcessor(pages = listOf(route))
        handleTargetChecking(route)
        if (route is DynamicPage<*>) {
            val target = route.target.split("/").toMutableList()
            val newDynamic = "{}"
            target.forEachIndexed { i, text ->
                if (text.startsWith("{")) {
                    target[i] = newDynamic
                }
            }
            dynamicRoutes[target] = route
        }

        return this
    }

    private fun handleTargetChecking(route: Page<*>) {
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

    fun addRoutes(routes: List<Page<*>>): Router {
        routes.forEach {
            addRoute(it)
        }
        return this
    }

    private fun handleDynamic(requestDTO: RequestDTO): ResponseDTO? {
        val target = requestDTO.target
        val url = target.split('/')
        if (url.contains("favicon.ico")) return null
        dynamicRoutes.forEach { (target, route) ->
            var matches = true
            target.forEachIndexed { i, pTarget ->
                try {
                    if (url[i] != pTarget) {
                        if (pTarget != "{}") {
                            matches = false
                            return@forEachIndexed
                        }
                    }
                } catch (_: Exception) {
                    return@forEachIndexed
                }
            }

            if (matches) {
                route.request = requestDTO
                return route.content().let { content ->
                    when (content) {
                        is ContentType.Response -> content.response
                        is ContentType.HtmlElements -> ResponseDTO(
                            status = 200,
                            statusText = "All is well",
                            headers = mutableMapOf("Content-Type" to "text/html"),
                            body = "<!doctype html><html><head>${route.metadata?.render()}</head><body>${content.htmlElement.render()}</body></html>"
                        )
                    }
                }
            }
        }
        return null
    }

    private fun handleResponse(page: Page<ContentType.Response>, client: Socket) {
        builder.build(page.content().response, client.getOutputStream())
    }

    private fun handleCasual(page: Page<ContentType.HtmlElements>, client: Socket, target: String) {
        val metadata = page.metadata
        val response = if (Cache.singleton.cache.containsKey(target)) {
            Cache.singleton.cache[target]!!
        } else {
            ResponseDTO(
                status = 200,
                statusText = "All is well",
                headers = mutableMapOf(
                    "Content-Type" to "text/html",
                ),
                body = "<!doctype html><html><head>${metadata?.render()}</head><body>${page.content().htmlElement.render()}</body></html>"
            )
        }

        builder.build(
            response = response,
            outputStream = client.getOutputStream()
        )
    }

    fun route(requestDTO: RequestDTO, client: Socket) {
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            val page = routes[target]
            page!!.request = requestDTO
            if (page.content() is ContentType.Response) {
                handleResponse(page as Page<ContentType.Response>, client)
            } else {
                handleCasual(page as Page<ContentType.HtmlElements>, client, target)
            }
        } else {
            val response = handleDynamic(requestDTO)
                ?: ResponseDTO(
                    status = 404,
                    statusText = "Not Found",
                    headers = mutableMapOf(
                        "Content-Type" to "text/html",
                    ),
                    body = "<!doctype html><html><body><h1>No Route Found!</h1></body></html>"
                )
            builder.build(
                response = response,
                outputStream = client.getOutputStream()
            )
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