package io.voidx.util

import io.voidx.clienthandler.ClientHandler
import io.voidx.dto.http.RequestDTO
import io.voidx.dto.http.ResponseDTO
import io.voidx.page.Page
import io.voidx.router.Router

/**
 * HTML integration hooks shared between the core server/router and the HTML module.
 *
 * The HTML module (see RouterUtil) sets these callbacks during initialization so the
 * base server/runtime can delegate responsibilities without a hard dependency.
 */

/**
 * Resolves a KTS request by target path. Invoked when the incoming request contains
 * the header "KTS-Request". The router is the receiver to allow access to routes
 * and helpers. Implementations should return an HTTP response.
 *
 * Params:
 * - target: the requested KTS endpoint path
 * - query: parsed query string parameters
 * - requestDTO: the current request
 * - clientHandler: the active client handler
 */
typealias GetKtsPageFn = Router.(String, Map<String, String>, RequestDTO, ClientHandler) -> ResponseDTO

/**
 * Hook used when a page is registered with the router. Allows the HTML module to
 * attach JS/CSS resources (Tailwind, KTS script, external CSS) to the page and
 * register the corresponding resource routes.
 */
typealias HandleJsAndCss = (Page, Router) -> Unit

/**
 * Mutable registry of HTML integration hooks. The HTML module sets these via
 * its module initializer; the core router consults them when adding routes or
 * serving KTS requests.
 */
object HtmlIntegration {
    /** Handler for KTS requests, if provided by the HTML module. */
    var getKtsPage: GetKtsPageFn? = null

    /** In-memory registry of generated JS pages to inject into HTML metadata. */
    val jsPages = mutableSetOf<Page>()

    /** Callback applied on each page addition to wire JS and CSS resources. */
    var handleJsAndCss: HandleJsAndCss? = null
}
