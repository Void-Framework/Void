package io.void.router

import io.void.api.CssPage
import io.void.api.JsPage
import io.void.api.KtsPage
import io.void.cache.CacheProcessor
import io.void.clienthandler.ClientHandler
import io.void.dto.http.*
import io.void.generator.TailwindGen
import io.void.html.exceptions.ExceptionPage
import io.void.html.exceptions.IExceptionPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.page.INullRoutePage
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
    private var exceptionPage = ExceptionPage(e = Exception())
    private var nullPage: Page<*>? = null
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
        val target = requestDTO.target
        val response =
            when {
                requestDTO.headers.containsKey("KTS-Request") && ktsResponsePages.containsKey(target) -> {
                    val page = ktsResponsePages[target] as KtsPage
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
                    page.request = requestDTO

                    page.middlewareProcessBefore(requestDTO.toResult())
                        ?: if (page.content() is ContentType.Response) {
                            handleResponse(page as Page<ContentType.Response>, clientHandler)
                        } else {
                            handleCasual(page as Page<ContentType.HtmlElements>, clientHandler, target)
                        }
                }

                else -> {
                    handleDynamic(requestDTO)
                        ?: if (nullPage != null) {
                            val page = nullPage as INullRoutePage
                            nullPage?.middlewareProcessBefore(requestDTO.toResult())
                                ?: when (nullPage!!.contentType) {
                                    ContentType.Response::class -> (nullPage!!.content() as ContentType.Response).response
                                    else ->
                                        buildResponse {
                                            status = 404
                                            statusText = page.statusText
                                            headers = page.headers.toMutableMap()
                                            body =
                                                "<!doctype html><html><head>${nullPage!!.metadata?.render()}</head><body>${(
                                                    nullPage!!
                                                        .content() as ContentType.HtmlElements
                                                ).htmlElement.render()}</body></html>"
                                        }
                                }
                        } else {
                            buildResponse {
                                status = 404
                                statusText = "Not Found"
                                headers { put("Content-Type", "text/html") }
                                body = "<!doctype html><html><body><h1>No Route Found!</h1></body></html>"
                            }
                        }
                }
            }

        val page = ktsResponsePages[target] ?: routes[target] ?: nullPage as? Page<*>
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
        exceptionPage.e = e
        var statusCode: Int? = null
        var log = true
        var statusMessage: String? = null
        var headers: MutableMap<String, String>? = null
        try {
            statusCode = exceptionPage.newPage.statusCode
            log = exceptionPage.newPage.logException
            statusMessage = exceptionPage.newPage.statusMessage
            headers = exceptionPage.newPage.headers
        } catch (_: Exception) {
        }
        client.getOutputStream().writeHTTP(
            response =
                buildResponse {
                    status = statusCode ?: 500
                    statusText = statusMessage ?: "Server Error"
                    headers {
                        put("Content-Type", "text/html")
                        put("Connection", "close")
                    }
                    headers?.let {
                        this.headers = it.toMutableMap()
                    }
                    body = exceptionPage.page
                },
            version = clientHandler.server.httpVersion,
        )

        if (log) {
            e.printStackTrace()
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
