package io.voidx.util

import io.voidx.ClientHandler
import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
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
 * Mutable registry of HTML integration hooks.
 *
 * The HTML module sets these via controlled registration functions; the core router
 * consults them when adding routes or serving KTS requests. This object prevents
 * arbitrary replacement of handlers and ensures read-only access to pages.
 */
object HtmlIntegration {

    /** Handler for KTS requests, if provided by the HTML module. */
    private var _getKtsPage: GetKtsPageFn? = null
    val getKtsPage: GetKtsPageFn? get() = _getKtsPage

    /** In-memory registry of generated JS pages to inject into HTML metadata. */
    private val _jsPages = mutableSetOf<Page>()
    val jsPages: Set<Page> get() = _jsPages

    /** Callback applied on each page addition to wire JS and CSS resources. */
    private var _handleJsAndCss: HandleJsAndCss? = null
    val handleJsAndCss: HandleJsAndCss? get() = _handleJsAndCss

    /**
     * Register the KTS page handler.
     *
     * Can only be called once. Throws an error if already registered.
     */
    fun registerKtsPage(fn: GetKtsPageFn) {
        check(_getKtsPage == null) { "getKtsPage already registered" }
        _getKtsPage = fn
    }

    /**
     * Register the JS/CSS handler.
     *
     * Can only be called once. Throws an error if already registered.
     */
    fun registerJsAndCss(fn: HandleJsAndCss) {
        check(_handleJsAndCss == null) { "handleJsAndCss already registered" }
        _handleJsAndCss = fn
    }

    /**
     * Add a generated JS page to the registry.
     *
     * This is read-only externally; pages can only be added via this method.
     */
    fun addJsPage(page: Page) {
        _jsPages += page
    }
}

