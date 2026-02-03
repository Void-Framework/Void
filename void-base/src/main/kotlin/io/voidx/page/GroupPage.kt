package io.voidx.page

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.emptyResponse

class GroupPage(
    override val target: String,
) : PageHandler(target) {
    override var request: RequestDTO
        get() = super.request
        set(value) {
            super.request = value
            routes.forEach { it.request = value }
        }
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

    override fun content(): ResponseDTO {
        val handledByChild =
            routes
                .find { child ->
                    when (child) {
                        is GroupPage -> child.target == request.target || request.target.startsWith(child.target)
                        else -> child.target == request.target
                    }
                }?.content()

        return handledByChild
            ?: responses[request.method]?.invoke(request)
            ?: emptyResponse()
    }
}

fun groupRoute(
    path: String,
    block: GroupPage.() -> Unit,
): GroupPage {
    val page = GroupPage(path)
    page.block()
    return page
}
