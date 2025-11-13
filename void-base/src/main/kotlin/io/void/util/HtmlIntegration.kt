package io.void.util

import io.void.clienthandler.ClientHandler
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.html.page.Page
import io.void.router.Router


typealias GetKtsPageFn = Router.(String, Map<String, String>, RequestDTO, ClientHandler) -> ResponseDTO
typealias HandleJsAndCss = (Page, Router) -> Unit

object HtmlIntegration {
    var getKtsPage: GetKtsPageFn? = null
    val jsPages = mutableSetOf<Page>()
    var handleJsAndCss: HandleJsAndCss? = null
}