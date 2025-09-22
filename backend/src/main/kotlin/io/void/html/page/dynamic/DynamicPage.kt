package io.void.html.page.dynamic

import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.Element
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.metadata

typealias Path = String

/**
 * Dynamic targets are initialized using this {a}
 *
 * Example: /web/test/{a}
 * Optional targets are initialized using this {a?}
 * - Optional targets may only be defined at the end, you also need to query it using the question mark
 * - Example: /web/{a?}/test -> query would be data["a?"]
 *
 * Example: /web/test/{a?}
 */
abstract class DynamicPage<T : ContentType>(
    target: String,
) : Page<T>(target = target) {
    internal var _data = mutableMapOf<Path, String>()

    val data: Map<Path, String> get() = _data
}

fun dynamicJsonRoute(
    path: String,
    block: Map<String, String?>.(RequestDTO) -> ResponseDTO,
): DynamicPage<ContentType.Response> =
    object : DynamicPage<ContentType.Response>(target = path) {
        override var metadata: Metadata? = null
        override val contentType = ContentType.Response::class

        override fun content() = ContentType.Response(block(data, request))
    }

fun dynamicHtmlRoute(
    path: String,
    metadata: Metadata.() -> Unit,
    block: (RequestDTO) -> Element,
): DynamicPage<ContentType.HtmlElements> =
    object : DynamicPage<ContentType.HtmlElements>(target = path) {
        private val _metadata = metadata(this) { }.apply(metadata)
        override var metadata: Metadata? = _metadata
        override val contentType = ContentType.HtmlElements::class

        override fun content() = ContentType.HtmlElements(block(request), _metadata)
    }
