package io.voidx.html.page

import io.voidx.dto.http.RequestDTO
import io.voidx.html.Element
import io.voidx.html.util.createResponse

/**
 * Base page for KTS-driven interactions.
 *
 * How it works:
 * - The HTML module installs a handler that recognizes requests containing the header
 *   "KTS-Request" (see RouterUtil). For such requests, the server resolves the original
 *   HTML page, reconstructs its DOM tree, and locates the elements referenced by the
 *   client-sent ids "KTS-Trigger" and "KTS-Target".
 * - Those elements are then exposed to the KTS page via [trigger] and [targetElement].
 * - Your KTS page [content] should return the fragment (root [Element]) that will be
 *   sent back to the client and injected according to the client-side KTS configuration.
 */
abstract class KtsPage(
    override val target: String,
) : Page(target) {
    /** The element that initiated the KTS action on the client, if provided. */
    internal var _trigger: Element? = null

    /** The element that should be updated on the client, if provided. */
    internal var _target: Element? = null

    /** Public accessor for the triggering element. */
    val trigger: Element?
        get() = _trigger

    /** Public accessor for the target element. */
    val targetElement: Element?
        get() = _target
}

/**
 * Defines a KTS route at [path]. When invoked, the [block] receives:
 * - the current [RequestDTO],
 * - the triggering [Element] (if any), and
 * - the target [Element] (if any) that the client asked to update.
 *
 * The block must return the root [Element] to send back to the client.
 */
fun ktsRoute(
    path: String,
    block: KtsPage.(RequestDTO, Element?, Element?) -> Element,
): KtsPage =
    object : KtsPage(target = path) {
        override fun content() = createResponse(block(request, trigger, targetElement))
    }
