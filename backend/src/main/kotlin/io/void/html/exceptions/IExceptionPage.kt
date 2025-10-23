package io.void.html.exceptions

interface IExceptionPage {
    var exception: Exception
    val logException: Boolean
    val statusCode: Int
    val statusMessage: String
    val headers: MutableMap<String, String>
}
