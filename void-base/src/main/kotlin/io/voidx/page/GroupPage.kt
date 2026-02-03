package io.voidx.page

import io.voidx.dto.ResponseDTO

class GroupPage(override val target: String) : DynamicPage(target) {

    private val routes = mutableSetOf<PageHandler>()

    fun route(
        path: String,
        builder: PageHandler.() -> Unit,
    ) {
        val page = PageHandler("$target$path")
        page.builder()
        page.relaysBefore += this.relaysBefore
        page.relaysAfter += this.relaysAfter
        routes.add(page)
    }

    override fun content(): ResponseDTO = routes.first { it.target == this.request.target }.content()
}

fun group(path: String, block: GroupPage.() -> Unit): GroupPage {
    val page = GroupPage(path)
    page.block()
    return page
}