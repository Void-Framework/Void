package io.void.html.page

import io.void.api.CssPage
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata
import io.void.middleware.Relay
import io.void.middleware.RelayAfter
import io.void.middleware.RelayBefore
import io.void.router.Router
import io.void.router.listResourcePaths
import io.void.router.readResourceText
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

abstract class Page<T : ContentType>(
    open val target: String,
) {
    val classAttributes: MutableMap<Element, List<String>> = mutableMapOf()

    lateinit var request: RequestDTO
    abstract val contentType: KClass<T>
    abstract var metadata: Metadata?
    private val cssFiles = mutableListOf<String>()
    internal val relaysBefore = mutableListOf<Relay>()
    internal val relaysAfter = mutableListOf<Relay>()

    abstract fun content(): T

    operator fun invoke(vararg cssFileName: String): Page<T> {
        listResourcePaths("css").forEach {
            if (it.split("/").last() in cssFileName) {
                cssFiles.add(it)
            }
        }
        return this
    }

    internal fun addCssToRouter(router: Router) {
        cssFiles.forEach {
            val uuid = UUID.randomUUID()
            router.addRoute(CssPage(uuid, readResourceText(it)))
            val path = "/css/$uuid/styles.css"
            metadata = metadata ?: metadata(this) { externalCss = mutableListOf(path) }
            metadata!!.externalCss =
                (metadata!!.externalCss ?: mutableListOf()).apply {
                    add(path)
                }
        }
    }

    fun before(relay: KClass<Relay>) {
        relaysBefore.add(relay.createInstance())
    }

    fun before(relay: Relay) {
        relaysBefore.add(relay)
    }

    fun after(relay: KClass<Relay>) {
        relaysAfter.add(relay.createInstance())
    }

    fun after(relay: Relay) {
        relaysAfter.add(relay)
    }

    internal fun middlewareProcessBefore(requestDTO: Result<RequestDTO>): ResponseDTO? {
        relaysBefore.forEach {
            val newResponse = (it as? RelayBefore)?.processBefore(requestDTO)
            if (newResponse != null) {
                return newResponse
            }
        }
        return null
    }

    internal fun middlewareProcessAfter(response: Result<ResponseDTO>) {
        relaysBefore.forEach {
            (it as? RelayAfter)?.processAfter(response)
        }
    }
}

fun htmlRoute(
    path: String,
    metadata: Metadata.() -> Unit,
    block: Page<ContentType.HtmlElements>.(RequestDTO) -> Element,
): Page<ContentType.HtmlElements> =
    object : Page<ContentType.HtmlElements>(target = path) {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata
        override val contentType = ContentType.HtmlElements::class

        override fun content() = ContentType.HtmlElements(block(request), _metadata)
    }

fun apiRoute(
    path: String,
    block: Page<ContentType.Response>.(RequestDTO) -> ResponseDTO,
): Page<ContentType.Response> =
    object : Page<ContentType.Response>(target = path) {
        override var metadata: Metadata? = null
        override val contentType = ContentType.Response::class

        override fun content() = ContentType.Response(block(request))
    }
