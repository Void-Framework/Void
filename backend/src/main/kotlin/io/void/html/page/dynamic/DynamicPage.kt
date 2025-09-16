package io.void.html.page.dynamic

import io.void.dto.http.RequestDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType

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
abstract class DynamicPage<T : ContentType>(target: String): Page<T>(target = target) {

    internal var _data = mutableMapOf<Path, String>()

    val data: Map<Path, String> get() = _data

}