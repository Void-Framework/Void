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
abstract class DynamicPage<T : ContentType>(target: String): Page<T>(target = target) {

    internal fun check(requestDTO: RequestDTO): String? {
        return if (requestDTO.target.contains(target.substringAfter("{"))) {
            requestDTO.target.substringAfter(target.substringAfter("{"))
        } else {
            null
        }
    }
}