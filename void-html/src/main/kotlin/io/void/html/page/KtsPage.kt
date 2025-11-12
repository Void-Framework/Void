package io.void.html.page

import io.void.dto.http.RequestDTO
import io.void.html.Element
import io.void.html.page.metadata.Metadata
import io.void.html.util.createResponse

/**
 * Base page for KTS-driven interactions. When a request is marked as a KTS request, the
 * router will set [trigger] and [targetElement] based on the DOM ids sent by the client.
 */
abstract class KtsPage(
    override val target: String,
) : Page(target) {
    override var metadata: Metadata? = null
    internal var _trigger: Element? = null
    internal var _target: Element? = null
    val trigger: Element?
        get() = _trigger
    val targetElement: Element?
        get() = _target
}

/**
 * Defines a KTS route at [path]. When invoked, the [block] receives the current [RequestDTO],
 * the triggering element (if any), and the target element (if any) that the client asked to update.
 * The block must return the root [Element] to send back to the client.
 */
fun ktsRoute(
    path: String,
    block: KtsPage.(RequestDTO, Element?, Element?) -> Element,
): KtsPage =
    object : KtsPage(target = path) {
        override fun content() = createResponse(block(request, trigger, targetElement))
    }
