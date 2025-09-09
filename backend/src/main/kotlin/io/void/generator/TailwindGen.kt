package io.void.generator

import io.void.api.CssPage
import io.void.html.Element
import io.void.html.attributes.AttributeNames
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.MetadataHandler
import io.void.router.Router
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

class TailwindGen {

    companion object {

        private lateinit var resourceFile: String
        private val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1) // Forces HTTP/2
            .build()

        internal fun grabTailwind() {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://cdn.jsdelivr.net/npm/tailwindcss@latest/dist/tailwind.min.css"))
                .GET()
                .build()
            val cResponse = client.send(request, HttpResponse.BodyHandlers.ofString())
            val body = cResponse.body()
            resourceFile = formatCss(body)
        }

        private fun formatCss(css: String): String {
            // Normalize whitespace and break into tokens
            val spaced = css
                .replace("{", "{\n")
                .replace("}", "\n}\n")
                .replace(";", ";\n")

            val lines = spaced.lines()
            val sb = StringBuilder()
            var indentLevel = 0

            for (rawLine in lines) {
                val line = rawLine.trim()
                if (line.isEmpty()) continue

                // Adjust indent for closing brace
                if (line == "}") indentLevel--

                // Append indented line
                sb.append("    ".repeat(indentLevel))
                    .append(line)
                    .append("\n")

                // Increase indent after opening brace
                if (line.endsWith("{")) indentLevel++
            }

            return sb.toString()
        }

        private fun putInTailwind(element: Element, page: Page<*>) {
            if (element.attributes.containsKey(AttributeNames.CLASS)) {
                page.classAttributes[element] = element.attributes[AttributeNames.CLASS]!!.split(" ")
            }
            element.children?.forEach {
                putInTailwind(it, page)
            }
        }
        private fun handleElements(element: Element, page: Page<ContentType.HtmlElements>) {
            if (element.attributes.containsKey(AttributeNames.CLASS)) {
                putInTailwind(element, page)
            }
            element.children?.forEach {
                putInTailwind(it, page)
            }
        }
        private fun handleClasses(page: Page<ContentType.HtmlElements>): MutableSet<String> {
            val attributes = mutableSetOf<String>()
            page.classAttributes.forEach { (_, attribute) ->
                attribute.forEach {
                    if (it.contains(":")) {
                        attributes.add(".${it.substringBefore(":")}\\:${it.substringAfter(":")}:${it.substringBefore(":")}")
                    } else {
                        attributes.add(".$it")
                    }
                }
            }
            return attributes
        }

        private fun handleMetadataAdding(page: Page<ContentType.HtmlElements>, uuid: UUID) {
            if (page.metadata == null) {
                page.metadata = MetadataHandler.create(page = page, builder = {
                    style = uuid
                })
            } else {
                page.metadata!!.style = uuid
            }
        }

        internal fun processTailwind(page: Page<ContentType.HtmlElements>, router: Router) {
            handleElements(page.content().htmlElement, page)
            val attributes = handleClasses(page)

            var currentLine = ""
            val processedLines = mutableListOf<String>()

            BufferedReader(StringReader(resourceFile)).use { reader ->
                reader.lines().forEach { line ->
                    if (!line.endsWith("}")) {
                        currentLine += line
                    } else {
                        processedLines.add("$currentLine    }")
                        currentLine = ""  // Reset currentLine after processing
                    }
                }
                // Don't forget to add the last line if it doesn't end with "}":
                if (currentLine.isNotEmpty()) {
                    processedLines.add(currentLine)
                }
            }

            // Don't forget to add the last line if it doesn't end with "},":
            if (currentLine.isNotEmpty()) {
                processedLines.add(currentLine)
            }

            val newProcessedLines = filterLines(processedLines, attributes)

            val uuid = UUID.randomUUID()
            val css = newProcessedLines.joinToString().filter { it != ',' }
            router.addRoute(CssPage(uuid, css))
            handleMetadataAdding(page, uuid)
        }

        private fun filterLines(processedLines: MutableList<String>, attributes: MutableSet<String>): List<String> {
            return processedLines.filter { line ->
                if (line.startsWith("::after") || line.startsWith("::before") ||
                    line.startsWith("html") || line.startsWith("body") || line.startsWith("*")) {
                    line
                }
                attributes.any { attribute ->
                    return@any if (line.contains("@media")) {
                        line.substringAfter("{").substringBefore("{").trim() == attribute
                    } else {
                        line.substringBefore("{").trim() == attribute
                    }
                }
            }.map {
                if (it.contains("@media")) {
                    return@map "$it}"
                } else {
                    return@map it
                }
            }
        }
    }
}