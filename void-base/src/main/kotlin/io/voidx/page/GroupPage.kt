package io.voidx.page

import io.voidx.dto.ResponseDTO
import io.voidx.dto.emptyResponse

class GroupPage(override val target: String) : PageHandler(target) {

    private val routes = mutableSetOf<PageHandler>()

    fun group(
        path: String,
        builder: GroupPage.() -> Unit,
    ) {
        val page = GroupPage("$target$path")
        page.builder()
        page.relaysBefore += this.relaysBefore
        page.relaysAfter += this.relaysAfter
        routes.add(page)
    }

    override fun content(): ResponseDTO = routes.firstOrNull { it.target == this.request.target }?.content() ?:
        responses[request.method]?.invoke(request) ?: emptyResponse()
}

fun groupRoute(path: String, block: GroupPage.() -> Unit): GroupPage {
    val page = GroupPage(path)
    page.block()
    return page
}