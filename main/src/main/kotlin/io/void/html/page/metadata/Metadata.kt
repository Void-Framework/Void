package io.void.html.page.metadata

import io.void.html.page.Page
import io.void.server.Server
import java.net.InetAddress
import java.net.Socket
import java.net.URL
import java.nio.charset.Charset

class Metadata internal constructor(page: Page<*>) {

    var title: String = "Void Page"
    var description: String = "This is the default description of a Void page"
    var favicon: Pair<URL, String>? = null
    var keywords: List<String> = listOf()
    var charset: Charset = Charsets.UTF_8
    var copyright: Pair<String, String> = "Void" to "© 2025 Void Page"
    var og: Pair<Triple<String, String, URL>, URL> = Triple(title, description, URL("https://picsum.photos/seed/example/300/200")) to URL("http://${InetAddress.getLocalHost().hostAddress}${page.target}")
    var siteVerification: String? = null
    var canonical: String = "http://${InetAddress.getLocalHost().hostAddress}/"
    var themeColor: String = "#ffffff"
    var robotRules: String = "noindex nofollow"

    var externalCss: List<URL>? = null
    var externalJS: Map<URL, Boolean>? = null

    fun render(): String {
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
                metaOG("image", og.first.third.toString()) +
                metaOG("url", og.second.toString()) +
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
                        js += "<script src=\"$link\" ${if (deferer) "defer" else ""}></script>"
                    }
                    js
                } else {
                    ""
                }
    }

    private fun meta(name: String, content: String): String {
        return "<meta name=\"$name\" content=\"$content\">"
    }

    private fun metaOG(name: String, content: String): String {
        return "<meta property=\"og:$name\" content=\"$content\">"
    }
}