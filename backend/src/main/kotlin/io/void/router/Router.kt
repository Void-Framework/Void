package io.void.router

import io.void.cache.Processor
import io.void.dto.http.Headers
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.api.CssPage
import io.void.api.JsPage
import io.void.clienthandler.ClientHandler
import io.void.generator.TailwindGen
import io.void.html.exceptions.ExceptionPage
import io.void.html.exceptions.IExceptionPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.middleware.Middleware
import io.void.router.page.INullRoutePage
import io.void.router.util.MiddlewareTime
import io.void.router.util.RequestHandler
import io.void.router.util.RouteCheck
import jdk.javadoc.internal.tool.resources.version
import java.io.File
import java.net.Socket
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile
import kotlin.reflect.KClass

class Router(private var middleware: List<Middleware>? = null): RouteCheck, RequestHandler {

    private val js = mutableSetOf<JsPage>()
    private val routes: ConcurrentHashMap<String, Page<*>> = ConcurrentHashMap()
    val styles: ConcurrentHashMap<String, Pair<UUID, String>> = ConcurrentHashMap()
    override val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage<*>> = ConcurrentHashMap()
    private var exceptionPage = ExceptionPage(e = Exception())
    private var nullPage: Page<*>? = null

    init {
        middleware = middleware?.sortedByDescending { it.priority }
        TailwindGen.grabTailwind()

        val resourceFolder = "static/js"
        val paths = listResourcePaths(resourceFolder)
        paths.forEach { path ->
            val content = readResourceText("/$path")
            js.add(JsPage(UUID.randomUUID(), content))
        }
        js.forEach { addRoute(it) }

    }

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
        if (route::class != CssPage::class) {
            if (route.contentType == ContentType.HtmlElements::class) {
                TailwindGen.processTailwind(route as Page<ContentType.HtmlElements>, this)
                JsPage.addToMetadata(route, js.toList())
            }
        }
        Processor.annotationProcessor(page = route)
        handleTargetChecking(route, routes)
        if (route is IExceptionPage) {
            exceptionPage = ExceptionPage(page = route)
        }
        if (route is INullRoutePage) {
            nullPage = route
        }
        if (route is DynamicPage<*>) {
            val target = route.target.split("/")
            dynamicRoutes[target] = route
        }

        return this
    }

    fun addRoutes(routes: List<Page<*>>): Router {
        routes.forEach {
            addRoute(route = it)
        }
        return this
    }

    fun route(requestDTO: RequestDTO, clientHandler: ClientHandler) {
        val client = clientHandler.client
        val response = middlewareProcess(requestDTO, MiddlewareTime.BEFORE)
        if (response != null) {
            ResponseDTO.build(
                response = response,
                outputStream = client.getOutputStream(),
                version = clientHandler.server.httpVersion
            )
            return
        }
        val target = requestDTO.target
        if (routes.containsKey(target)) {
            val page = routes[target]
            page!!.request = requestDTO
            if (page.content() is ContentType.Response) {
                handleResponse(
                    page = page as Page<ContentType.Response>,
                    clientHandler = clientHandler
                )
            } else {
                handleCasual(
                    page = page as Page<ContentType.HtmlElements>,
                    clientHandler = clientHandler,
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
            ResponseDTO.build(
                response = response,
                outputStream = client.getOutputStream(),
                version = clientHandler.server.httpVersion
            )
            val lateResponse = middlewareProcess(
                requestDTO = requestDTO,
                type = MiddlewareTime.AFTER
            )
            if (lateResponse != null) {
                ResponseDTO.build(
                    response = lateResponse,
                    outputStream = client.getOutputStream(),
                    version = clientHandler.server.httpVersion
                )
            }
        }
    }

    fun error(clientHandler: ClientHandler, e: Exception) {
        val client = clientHandler.client
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
        ResponseDTO.build(
            response = ResponseDTO(status = statusCode ?: 500,
                statusText = statusMessage ?: "Server Error",
                headers = headers ?: mutableMapOf(
                    "Content-Type" to "text/html",
                    "Connection" to "close"
                ),
                body = exceptionPage.page),
            outputStream = client.getOutputStream(),
            version = clientHandler.server.httpVersion
        )

        if (log) {
            e.printStackTrace()
        }
    }
}

private fun listResourcePaths(folder: String): List<String> {
    val cl = Thread.currentThread().contextClassLoader
    val url = cl.getResource(folder) ?: return emptyList()
    return when (url.protocol) {
        "file" -> {
            val root = File(url.toURI())
            root.walkTopDown()
                .filter { it.isFile && it.extension == ".js" }
                .map { "$folder/" + it.relativeTo(root).invariantSeparatorsPath }
                .toList()
        }
        "jar" -> {
            val path = url.path
            val jarPath = path.substringAfter("file:").substringBefore("!")
            JarFile(URLDecoder.decode(jarPath, "UTF-8")).use { jar ->
                jar.entries().asSequence()
                    .map { it.name }
                    .filter { it.startsWith("$folder/") && !it.endsWith("/") &&
                            it.substringAfterLast('/').endsWith(".js", ignoreCase = true)
                    }
                    .toList()
            }
        }
        else -> emptyList()
    }
}

private fun readResourceText(path: String): String {
    return Router::class.java.getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")
}
