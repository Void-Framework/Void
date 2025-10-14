package io.void.router

import io.void.api.CssPage
import io.void.api.JsPage
import io.void.api.KtsPage
import io.void.cache.CacheProcessor
import io.void.clienthandler.ClientHandler
import io.void.dto.http.*
import io.void.generator.TailwindGen
import io.void.html.page.ExceptionPage
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.dynamic.DynamicPage
import io.void.html.page.dynamic.Path
import io.void.html.page.exceptionPage
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.page.INullRoutePage
import io.void.router.page.PageHandler
import io.void.router.util.MiddlewareTime
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
    private var exceptionPage: ExceptionPage<*> = exceptionPage { e ->
        return@exceptionPage buildResponse {
            status = 500
            statusText = "Server Error"
            headers {
                put("Content-Type", "text/html")
                put("Connection", "close")
            }
            body = "<!doctype html><html>" +
                    "<head>" +
                    "  <style>" +
                    "#__next-dev-overlay {\n" +
                    "  position: fixed;\n" +
                    "  top: 0;\n" +
                    "  left: 0;\n" +
                    "  width: 100%;\n" +
                    "  height: 100%;\n" +
                    "  background: rgba(0, 0, 0, 0.8);\n" +
                    "  color: #fff;\n" +
                    "  font-family: system-ui, sans-serif;\n" +
                    "  z-index: 2147483647; /* Ensures it stays on top */\n" +
                    "}\n" +
                    "\n" +
                    "/* Main overlay styling */\n" +
                    ".overlay {\n" +
                    "  max-width: 800px;\n" +
                    "  margin: 50px auto;\n" +
                    "  background: #1e1e1e;\n" +
                    "  padding: 20px;\n" +
                    "  border-radius: 4px;\n" +
                    "  box-shadow: 0 2px 10px rgba(0,0,0,0.3);\n" +
                    "}\n" +
                    "\n" +
                    "/* Header section with title and close button */\n" +
                    ".overlay__header {\n" +
                    "  display: flex;\n" +
                    "  justify-content: space-between;\n" +
                    "  align-items: center;\n" +
                    "  margin-bottom: 15px;\n" +
                    "}\n" +
                    "\n" +
                    ".overlay__title {\n" +
                    "  font-size: 1.5em;\n" +
                    "  font-weight: bold;\n" +
                    "}\n" +
                    "\n" +
                    ".overlay__close {\n" +
                    "  background: transparent;\n" +
                    "  border: none;\n" +
                    "  font-size: 1.5em;\n" +
                    "  color: #fff;\n" +
                    "  cursor: pointer;\n" +
                    "}\n" +
                    "\n" +
                    "/* Styling for the error message and stack trace */\n" +
                    ".overlay__content {\n" +
                    "  color: #FF4C4C;\n" +
                    "}\n" +
                    ".error-message pre,\n" +
                    ".stack-trace pre {\n" +
                    "  margin: 0;\n" +
                    "  padding: 10px;\n" +
                    "  overflow: auto;\n" +
                    "  background: #2d2d2d;\n" +
                    "  border-radius: 4px;\n" +
                    "  font-size: 0.9em;\n" +
                    "}" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "<div id=\"__next-dev-overlay\">\n" +
                    "  <div class=\"overlay\">\n" +
                    "    <div class=\"overlay__header\">\n" +
                    "      <span class=\"overlay__title\">${e::class.simpleName}: ${e.localizedMessage}</span>\n" +
                    "    </div>\n" +
                    "    <div class=\"overlay__content\">\n" +
                    "      <div class=\"error-message\">\n" +
                    "        <pre>${e::class.simpleName}: ${e.localizedMessage}</pre>\n" +
                    "      </div>\n" +
                    "      <div class=\"stack-trace\">\n" +
                    "        <pre>\n" +
                    "          ${e.stackTrace.joinToString("\n")}\n" +
                    "        </pre>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>" +
                    "</body>" +
                    "</html>"
        }
    }
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

    internal fun middlewareProcess(
        requestDTO: Result<RequestDTO>,
        type: MiddlewareTime,
    ): ResponseDTO? {
        internalRelay.forEach {
            val newResponse =
                when (type) {
                    MiddlewareTime.BEFORE -> (it as? RelayBefore)?.processBefore(requestDTO)
                    MiddlewareTime.AFTER -> (it as? RelayAfter)?.processAfter(requestDTO)
                }
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
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
            exceptionPage = route
            return this
        }
        if (route is INullRoutePage) {
            nullPage = route
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
        val response = middlewareProcess(requestDTO.toResult(), MiddlewareTime.BEFORE)
        if (response != null) {
            client.getOutputStream().writeHTTP(
                response = response,
                version = clientHandler.server.httpVersion,
            )
            return
        }
        val target = requestDTO.target
        if (requestDTO.headers.containsKey("KTS-Request") && ktsResponsePages.containsKey(target)) {
            val page = ktsResponsePages[target] as KtsPage
            val route = requestDTO.headers["KTS-Route"]!!
            val rootElement = (routes[route]!!.content() as? ContentType.HtmlElements)?.htmlElement
            val triggerId = requestDTO["KTS-Trigger"]
            val targetId = requestDTO["KTS-Target"]
            val trigger = triggerId?.let { rootElement?.findElement(it) }
            val target = targetId?.let { rootElement?.findElement(it) }
            page._target = target
            page._trigger = trigger
            page.request = requestDTO
            handleKts(
                page = page,
                clientHandler = clientHandler,
            )
        }
        if (routes.containsKey(target)) {
            val page = routes[target]
            page!!.request = requestDTO
            if (page.content() is ContentType.Response) {
                handleResponse(
                    page = page as Page<ContentType.Response>,
                    clientHandler = clientHandler,
                )
            } else {
                handleCasual(
                    page = page as Page<ContentType.HtmlElements>,
                    clientHandler = clientHandler,
                    target = target,
                )
            }
        } else {
            val response =
                handleDynamic(requestDTO)
                    ?: if (nullPage != null) {
                        val response = ContentType.Response::class
                        val page = nullPage as INullRoutePage
                        when (nullPage!!.contentType) {
                            response -> (nullPage!!.content() as ContentType.Response).response
                            else ->
                                buildResponse {
                                    status = 404
                                    statusText = page.statusText
                                    headers = page.headers.toMutableMap()
                                    body =
                                        "<!doctype html><html><head>${nullPage!!.metadata?.render()}</head><body>${(nullPage!!.content() as ContentType.HtmlElements).htmlElement.render()}</body></html>"
                                }
                        }
                    } else {
                        buildResponse {
                            status = 404
                            statusText = "Not Found"
                            headers {
                                put("Content-Type", "text/html")
                            }
                            body = "<!doctype html><html><body><h1>No Route Found!</h1></body></html>"
                        }
                    }
            client.getOutputStream().writeHTTP(
                response = response,
                version = clientHandler.server.httpVersion,
            )
            val lateResponse =
                middlewareProcess(
                    requestDTO = requestDTO.toResult(),
                    type = MiddlewareTime.AFTER,
                )
            if (lateResponse != null) {
                client.getOutputStream().writeHTTP(
                    response = lateResponse,
                    version = clientHandler.server.httpVersion,
                )
            }
        }
    }

    fun error(
        clientHandler: ClientHandler,
        e: Exception,
    ) {
        val client = clientHandler.client
        exceptionPage.exception = e
        when (val content = exceptionPage.content()) {
            is ContentType.Response -> client.getOutputStream().writeHTTP(
                response = content.response,
                version = clientHandler.server.httpVersion,
            )
            is ContentType.HtmlElements -> client.getOutputStream().writeHTTP(
                response = buildResponse {
                    status = 200
                    statusText = "All is well"
                    headers {
                        put("Content-Type", "text/html")
                    }
                    body =
                        """
                <!doctype html><html>
                <head>${content.metadata.render()}</head>
                <body>${content.htmlElement.render()}</body>
                </html>
                """.trimIndent()
                },
                version = clientHandler.server.httpVersion
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
