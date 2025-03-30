package io.void.router

import io.void.cache.Cache
import io.void.cache.Processor
import io.void.dto.http.Headers
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.exceptions.ExceptionPage
import io.void.html.exceptions.IExceptionPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.http.builder.HTTPBuilder
import io.void.middleware.Middleware
import io.void.router.exceptions.RouteNoTargetException
import io.void.router.exceptions.RouteTargetUsedException
import io.void.router.page.INullRoutePage
import io.void.router.util.MiddlewareTime
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class Router(private var middleware: List<Middleware>? = null) {

    init {
        middleware = middleware?.sortedByDescending { it.priority }
    }

    private val routes: ConcurrentHashMap<String, Page<*>> = ConcurrentHashMap()
    private val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage<*>> = ConcurrentHashMap()
    private val builder = HTTPBuilder()
    private var exceptionPage = ExceptionPage(e = Exception())
    private var nullPage: Page<*>? = null

    private fun middlewareProcess(requestDTO: RequestDTO, type: MiddlewareTime): ResponseDTO? {
        middleware?.forEach {
            val newResponse = when (type) {
                MiddlewareTime.BEFORE -> it.processBefore(requestDTO)
                MiddlewareTime.AFTER -> it.processAfter(requestDTO)
            }
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
    }

    fun middlewareHandleError(e: Exception): ResponseDTO? {
        middleware?.forEach {
            val newResponse = it.handleError(e)
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
    }

    //Add a function to add routes without finding the annotations
    fun addRoute(route: Page<*>): Router {
        Processor.annotationProcessor(page = route)
        handleTargetChecking(route)
        if (route is IExceptionPage) {
            exceptionPage = ExceptionPage(page = route)
        }
        if (route is INullRoutePage) {
            nullPage = route
        }
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
            throw RouteTargetUsedException(target = route.target)
        } else {
            if (route.target.startsWith("/")) {
                routes[route.target] = route
            } else {
                throw RouteNoTargetException(target = route.target)
            }
        }
    }

    fun addRoutes(routes: List<Page<*>>): Router {
        routes.forEach {
            addRoute(route = it)
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
                        is ContentType.HtmlElements -> constructClassicResponse(page = route)
                    }
                }
            }
        }
        return null
    }

    private fun<T : Page<*>> constructClassicResponse(page: T): ResponseDTO {
        return ResponseDTO(
            status = 200,
            statusText = "All is well",
            headers = mutableMapOf("Content-Type" to "text/html"),
            body = "<!doctype html><html><head>${page.metadata?.render()}</head><body>${(page.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
        )
    }

    private fun handleResponse(page: Page<ContentType.Response>, client: Socket) {
        builder.build(
            response = page.content().response,
            outputStream = client.getOutputStream()
        )
    }

    private fun handleCasual(page: Page<ContentType.HtmlElements>, client: Socket, target: String) {
        val response = if (Cache.singleton.cache.containsKey(target)) {
            Cache.singleton.cache[target]!!
        } else {
            constructClassicResponse(
                page = page
            )
        }

        builder.build(
            response = response,
            outputStream = client.getOutputStream()
        )
    }

    fun route(requestDTO: RequestDTO, client: Socket) {
        middleware?.let {
            val response = middlewareProcess(requestDTO, MiddlewareTime.BEFORE)
            if (response != null) {
                builder.build(
                    response = response,
                    outputStream = client.getOutputStream()
                )
                return
            }
        }
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            val page = routes[target]
            page!!.request = requestDTO
            if (page.content() is ContentType.Response) {
                handleResponse(
                    page = page as Page<ContentType.Response>,
                    client = client
                )
            } else {
                handleCasual(
                    page = page as Page<ContentType.HtmlElements>,
                    client = client,
                    target = target
                )
            }
        } else {
            val response = handleDynamic(requestDTO)
                ?: if (nullPage != null) {
                    val response = ContentType.Response::class
                    val page = nullPage as INullRoutePage
                    when (nullPage!!.contentType) {
                        response -> (nullPage!!.content() as ContentType.Response).response
                        else -> ResponseDTO(
                            status = 404,
                            statusText = page.statusText,
                            headers = page.headers,
                            body = "<!doctype html><html><head>${nullPage!!.metadata?.render()}</head><body>${(nullPage!!.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
                        )
                    }
                } else {
                    ResponseDTO(
                        status = 404,
                        statusText = "Not Found",
                        headers = mutableMapOf(
                            "Content-Type" to "text/html",
                        ),
                        body = "<!doctype html><html><body><h1>No Route Found!</h1></body></html>"
                    )
                }
            builder.build(
                response = response,
                outputStream = client.getOutputStream()
            )
            val lateResponse = middlewareProcess(
                requestDTO = requestDTO,
                type = MiddlewareTime.AFTER
            )
            if (lateResponse != null) {
                builder.build(
                    response = lateResponse,
                    outputStream = client.getOutputStream()
                )
            }
        }
    }

    fun error(client: Socket, e: Exception) {
        exceptionPage.e = e
        var statusCode: Int? = null
        var log = true
        var statusMessage: String? = null
        var headers: Headers? = null
        try {
            statusCode = exceptionPage.newPage.statusCode
            log = exceptionPage.newPage.logException
            statusMessage = exceptionPage.newPage.statusMessage
            headers = exceptionPage.newPage.headers
        } catch (_: Exception) {

        }
        builder.build(
            response = ResponseDTO(status = statusCode ?: 500,
                statusText = statusMessage ?: "Server Error",
                headers = headers ?: mutableMapOf(
                    "Content-Type" to "text/html",
                    "Connection" to "close"
                ),
                body = exceptionPage.page),
            outputStream = client.getOutputStream())

        if (log) {
            e.printStackTrace()
        }
    }
}