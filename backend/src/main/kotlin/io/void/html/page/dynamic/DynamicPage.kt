package io.void.html.page.dynamic

import io.void.dto.http.RequestDTO
import io.void.html.page.Page
import io.void.html.page.content.ContentType

/**
 * Dynamic targets are initialized using this {a}
 *
 * Example: /web/test/{a}
 *
 */

typealias Path = String

abstract class DynamicPage<T : ContentType>(target: String): Page<T>(target = target) {

    internal var _data = mutableMapOf<Path, String>()

    val data: Map<Path, String> get() = _data

}