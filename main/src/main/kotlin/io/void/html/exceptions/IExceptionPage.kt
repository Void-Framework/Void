package io.void.html.exceptions

import io.void.dto.Headers

interface IExceptionPage {

    var exception: Exception
    val logException: Boolean
    val statusCode: Int
    val statusMessage: String
    val headers: Headers
}