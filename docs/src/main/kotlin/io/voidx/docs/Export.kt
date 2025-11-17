package io.voidx.docs

import io.voidx.api.method.Method
import io.voidx.dto.http.ResponseBody
import io.voidx.dto.http.buildRequest
import io.voidx.dto.http.headers
import io.voidx.html.page.metadata
import io.voidx.router.router
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

    // Create a minimal router, add the docs page so hooks run (Tailwind + external CSS wiring)
    val router = router {
        +docsHomeRoute
    }

    // Prepare a minimal GET request to bind to the page before rendering
    val pageReq =
        buildRequest {
            method = Method.GET
            target = "/"
            headers { put("User-Agent", "exporter") }
            body = ""
        }

    // First render the docs home page so metadata.render() runs and populates externalCss links
    docsHomeRoute.request = pageReq
    val response = docsHomeRoute.content()
    val homeHtml =
        when (val b = response.body) {
            is ResponseBody.StringBody -> b.body
            is ResponseBody.ByteArrayBody -> String(b.body)
        }

    // Now that metadata has been rendered, export any generated CSS assets
    val cssLinks = docsHomeRoute.metadata!!.externalCss
    cssLinks?.forEach { href ->
        val route = router.routes[href]
        if (route != null) {
            // Render CSS by invoking the route content with a GET request
            route.request = buildRequest {
                method = Method.GET
                target = href
            }
            val cssResp = route.content()
            val cssBody =
                when (val b = cssResp.body) {
                    is ResponseBody.StringBody -> b.body
                    is ResponseBody.ByteArrayBody -> String(b.body)
                }
            val cssOut = outDir.resolve(href.removePrefix("/"))
            Files.createDirectories(cssOut.parent)
            write(cssOut, cssBody)
        }
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
