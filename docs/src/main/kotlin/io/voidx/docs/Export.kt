package io.voidx.docs

import io.voidx.dto.http.ResponseBody
import io.voidx.dto.http.buildRequest
import io.voidx.dto.http.headers
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

/**
 * Exports the docs server pages to static HTML files for GitHub Pages.
 * Currently exports the home route to build/pages/index.html.
 */
fun main() {
    val outDir = Path.of("build", "pages")
    Files.createDirectories(outDir)

    // Prepare a minimal GET request to bind to the page before rendering
    val req =
        buildRequest {
            method = io.voidx.api.method.Method.GET
            target = "/"
            headers { put("User-Agent", "exporter") }
            body = ""
        }

    // Render the docs home page using the same server page DSL
    docsHomeRoute.request = req
    val response = docsHomeRoute.content()
    val homeHtml =
        when (val b = response.body) {
            is ResponseBody.StringBody -> b.body
            is ResponseBody.ByteArrayBody -> String(b.body)
        }

    write(outDir.resolve("index.html"), homeHtml)

    println("[docs/export] Exported pages to ${outDir.toAbsolutePath()}")
}

private fun write(path: Path, content: String) {
    Files.write(
        path,
        content.toByteArray(),
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE,
    )
}
