package io.void.html.page.metadata

interface MetadataHandler {

    fun create(builder: Metadata.() -> Unit): Metadata {
        val metadata = Metadata()
        metadata.apply(builder)
        return metadata
    }
}