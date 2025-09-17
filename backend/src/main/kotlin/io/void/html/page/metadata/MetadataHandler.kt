package io.void.html.page.metadata

import io.void.html.page.Page

interface MetadataHandler {
    companion object {
        fun create(
            builder: Metadata.() -> Unit,
            page: Page<*>,
        ): Metadata {
            val metadata = Metadata(page)
            metadata.apply(builder)
            return metadata
        }
    }
}
