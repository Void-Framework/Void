package io.void.html.metadata

import io.void.html.page.Page
import java.net.InetAddress
import java.nio.charset.Charset
import java.util.*

/**
 * Describes HTML document metadata for a [Page]: title, description, icons, social tags,
 * canonical URL, theme color, robots, external CSS/JS, and arbitrary raw tags.
 *
 * Instances are normally created via [metadata] DSL and rendered into the <head> by the router.
 */
class Metadata internal constructor(
    page: Page,
) {
    var title: String = "Void Page"
    var description: String = "This is the default description of a Void page"
    var favicon: Pair<String, String>? = null
    var keywords: List<String> = listOf()
    var charset: Charset = Charsets.UTF_8
    var copyright: Pair<String, String> = "Void" to "© 2025 Void Page"
    var og: Pair<Triple<String, String, String>, String> =
        Triple(title, description, "https://picsum.photos/seed/example/300/200") to
            "http://${InetAddress.getLocalHost().hostAddress}${page.target}"
    var siteVerification: String? = null
    var canonical: String = "http://${InetAddress.getLocalHost().hostAddress}/"
    var themeColor: String = "#ffffff"
    var robotRules: String = "noindex nofollow"

    var externalCss: MutableList<String>? = null
    var externalJS: MutableMap<String, Boolean>? = null
    internal var style: UUID? = null
    val rawTags = mutableListOf<String>()

    /** Renders this metadata into a string of <head> tags ready to be embedded in an HTML document. */
    internal fun render(): String {
        handleStyles()
        return "<title>$title</title>" +
            meta("description", description) +
            if (favicon != null) {
                "<link rel=\"icon\" href=\"${favicon!!.first}\" type=\"${favicon!!.second}\">"
            } else {
                ""
            } +
            meta("keywords", keywords.joinToString()) +
            "<meta charset=\"$charset\">" +
            meta("author", copyright.first) +
            meta("copyright", copyright.second) +
            metaOG("title", og.first.first) +
            metaOG("description", og.first.second) +
            metaOG("image", og.first.third) +
            metaOG("url", og.second) +
            if (siteVerification != null) {
                meta("google-site-verification", siteVerification!!)
            } else {
                ""
            } +
            "<link rel=\"canonical\" href=\"$canonical\">" +
            meta("theme-color", themeColor) +
            meta("robots", robotRules) +
            if (externalCss != null) {
                var css = ""
                externalCss!!.forEach { link ->
                    css += "<link rel=\"stylesheet\" href=\"$link\">\n"
                }
                css
            } else {
                ""
            } +
            if (externalJS != null) {
                var js = ""
                externalJS!!.forEach { (link, deferer) ->
                    js += "<script src=\"$link\" ${if (deferer) "defer" else ""}></script>\n"
                }
                js
            } else {
                ""
            } + rawTags.joinToString("\n")
    }

    private fun meta(
        name: String,
        content: String,
    ): String = "<meta name=\"$name\" content=\"$content\">"

    private fun metaOG(
        name: String,
        content: String,
    ): String = "<meta property=\"og:$name\" content=\"$content\">"

    private fun handleStyles() {
        val styleId = style ?: return
        if (externalCss == null) {
            externalCss = mutableListOf("/css/$styleId/styles.css")
        } else {
            externalCss!!.add("/css/$styleId/styles.css")
        }
    }
}

/**
 * DSL entry point to create [Metadata] for the given [page] using [builder].
 */
fun metadata(
    page: Page,
    builder: Metadata.() -> Unit,
): Metadata {
    val metadata = Metadata(page)
    metadata.apply(builder)
    return metadata
}
