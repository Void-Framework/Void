package io.void.router

import io.void.api.CssPage
import io.void.api.JsPage
import io.void.api.KtsPage
import io.void.cache.CacheProcessor
import io.void.clienthandler.ClientHandler
import io.void.dto.http.*
import io.void.generator.TailwindGen
import io.void.html.page.ExceptionPage
import io.void.html.page.NotFoundPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.page.PageHandler
import io.void.router.util.RequestHandler
import io.void.router.util.RouteCheck
import java.io.File
import java.net.URLDecoder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile

class Router :
    RouteCheck,
    RequestHandler {
    private var internalRelay: List<Relay> = emptyList()
    val relay = mutableSetOf<Relay>()

    private val js = mutableSetOf<JsPage>()
    private val routes: ConcurrentHashMap<String, Page<*>> = ConcurrentHashMap()
    override val dynamicRoutes: ConcurrentHashMap<List<String>, DynamicPage<*>> = ConcurrentHashMap()
    private val ktsResponsePages = mutableMapOf<String, KtsPage>()

    init {
        recomputeMiddlewareSnapshot()
        TailwindGen.grabTailwind()

        val paths = listResourcePaths("js")
        paths.forEach { path ->
            val content = readResourceText("/$path", this::class.java)
            js.add(JsPage(UUID.randomUUID(), content))
        }
        js.forEach { addRoute(it) }
    }

    operator fun Page<*>.unaryPlus() {
        addRoute(this)
    }

    operator fun Relay.unaryPlus() {
        relay.add(this)
        recomputeMiddlewareSnapshot()
    }

    private fun recomputeMiddlewareSnapshot() {
        internalRelay = relay.sortedByDescending { it.priority }
    }

    internal fun middlewareProcessBefore(requestDTO: Result<RequestDTO>): ResponseDTO? {
        internalRelay.forEach {
            val newResponse = (it as? RelayBefore)?.processBefore(requestDTO)
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
    }

    internal fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        internalRelay.forEach {
            (it as? RelayAfter)?.processAfter(response)
        }
    }

    internal fun addRoute(route: Page<*>): Router {
        route.addCssToRouter(this)
        if (route::class != CssPage::class) {
            if (route.contentType == ContentType.HtmlElements::class) {
                route.request = buildRequest { }
                TailwindGen.processTailwind(route as Page<ContentType.HtmlElements>, this)
                JsPage.addToMetadata(route, js.toList())
            }
            if (route is KtsPage) {
                ktsResponsePages[route.target] = route
                CacheProcessor.processCacheables(page = route)
                return this
            }
        }
        CacheProcessor.processCacheables(page = route)
        if (route is ExceptionPage) {
            RouteCheck.exceptionPage = route
            return this
        }
        if (route is NotFoundPage) {
            RouteCheck.nullPage = route
            return this
        }
        handleTargetChecking(route, routes)
        if (route is DynamicPage<*>) {
            val target = route.target.split("/")
            dynamicRoutes[target] = route
        }

        return this
    }

    internal fun addRoutes(routes: List<Page<*>>): Router {
        routes.forEach {
            addRoute(route = it)
        }
        return this
    }

    fun route(
        requestDTO: RequestDTO,
        clientHandler: ClientHandler,
    ) {
        val client = clientHandler.client
        val target = requestDTO.target.substringBefore('?')
        val query =
            requestDTO.target
                .substringAfter("?", "")
                .split("&")
                .mapNotNull {
                    val parts = it.split("=", limit = 2)
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()
        val response =
            when {
                requestDTO.headers.containsKey("KTS-Request") && ktsResponsePages.containsKey(target) -> {
                    val page = ktsResponsePages[target] as KtsPage
                    page.queries = query
                    val route = requestDTO.headers["KTS-Route"]!!
                    val rootElement = (routes[route]!!.content() as? ContentType.HtmlElements)?.htmlElement
                    val triggerId = requestDTO["KTS-Trigger"]
                    val targetId = requestDTO["KTS-Target"]
                    val trigger = triggerId?.let { rootElement?.findElement(it) }
                    val targetEl = targetId?.let { rootElement?.findElement(it) }
                    page._target = targetEl
                    page._trigger = trigger
                    page.request = requestDTO

                    page.middlewareProcessBefore(requestDTO.toResult())
                        ?: handleKts(page, clientHandler)
                }

                routes.containsKey(target) -> {
                    val page = routes[target]!!
                    page.queries = query
                    page.request = requestDTO

                    page.middlewareProcessBefore(requestDTO.toResult())
                        ?: if (page.content() is ContentType.Response) {
                            handleResponse(page as Page<ContentType.Response>, clientHandler)
                        } else {
                            handleCasual(page as Page<ContentType.HtmlElements>, clientHandler, target)
                        }
                }

                else -> {
                    handleDynamic(requestDTO, query)
                        ?: run {
                            val page = RouteCheck.nullPage
                            page.queries = query
                            page.request = requestDTO
                            page.middlewareProcessBefore(requestDTO.toResult())
                                ?: when (page.contentType) {
                                    ContentType.Response::class -> (page!!.content() as ContentType.Response).response
                                    else ->
                                        buildResponse {
                                            status = 404
                                            statusText = "Not Found"
                                            headers {
                                                put("Content-Type", "text/html")
                                                put("Connection", "close")
                                            }
                                            body =
                                                "<!doctype html><html><head>${RouteCheck.nullPage.metadata?.render()}</head><body>${(
                                                    RouteCheck.nullPage
                                                        .content() as ContentType.HtmlElements
                                                ).htmlElement.render()}</body></html>"
                                        }
                                }
                        }
                }
            }

        val page = ktsResponsePages[target] ?: routes[target] ?: RouteCheck.nullPage as? Page<*>
        page?.middlewareProcessAfter(response.toResult())

        middlewareProcessAfter(
            response.toResult(),
        )
        client.getOutputStream().writeHTTP(
            response,
            clientHandler.server.httpVersion,
        )
    }

    fun error(
        clientHandler: ClientHandler,
        e: Exception,
    ) {
        val client = clientHandler.client
        RouteCheck.exceptionPage.exception = e
        when (val content = RouteCheck.exceptionPage.content()) {
            is ContentType.Response ->
                client.getOutputStream().writeHTTP(
                    response = content.response,
                    version = clientHandler.server.httpVersion,
                )
            is ContentType.HtmlElements ->
                client.getOutputStream().writeHTTP(
                    response =
                        buildResponse {
                            status = 500
                            statusText = "Server Error"
                            headers {
                                put("Content-Type", "text/html")
                                put("Connection", "close")
                            }
                            body =
                                """
                                <!doctype html><html>
                                <head>${content.metadata.render()}</head>
                                <body>${content.htmlElement.render()}</body>
                                </html>
                                """.trimIndent()
                        },
                    version = clientHandler.server.httpVersion,
                )
        }
    }

    fun on(path: String): PageHandler =
        if (routes.containsKey(path)) {
            routes[path] as PageHandler
        } else {
            val page = PageHandler(path)
            addRoute(page)
            page
        }
}

fun listResourcePaths(folder: String): List<String> {
    val cl = Thread.currentThread().contextClassLoader
    val url = cl.getResource(folder) ?: return emptyList()
    return when (url.protocol) {
        "file" -> {
            val root = File(url.toURI())
            root
                .walkTopDown()
                .filter { it.isFile }
                .map { "$folder/" + it.relativeTo(root).invariantSeparatorsPath }
                .toList()
        }
        "jar" -> {
            val path = url.path
            val jarPath = path.substringAfter("file:").substringBefore("!")
            JarFile(URLDecoder.decode(jarPath, "UTF-8")).use { jar ->
                jar
                    .entries()
                    .asSequence()
                    .map { it.name }
                    .filter {
                        it.startsWith("$folder/") && !it.endsWith("/")
                    }.toList()
            }
        }
        else -> emptyList()
    }
}

fun router(builder: Router.() -> Unit): Router {
    val router = Router().apply(builder)
    return router
}

fun <T> T.toResult(): Result<T> = Result.success(this)

fun <T> Exception.toResult(): Result<T> = Result.failure<T>(this)

fun readResourceText(
    path: String,
    clazz: Class<*>,
): String =
    clazz
        .getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")

fun readResourceText(path: String): String =
    Thread
        .currentThread()
        .contextClassLoader
        .getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")
