package io.void.generator

import io.void.api.CssPage
import io.void.html.Element
import io.void.html.attributes.AttributeNames
import io.void.html.page.Page
import io.void.router.Router
import java.io.File
import java.nio.file.Files
import java.util.*

class TailwindGen {

    companion object {

        private fun putInTailwind(element: Element, page: Page) {
            if (element.attributes.containsKey(AttributeNames.CLASS)) {
                page.classAttributes[element] = element.attributes[AttributeNames.CLASS]!!.split(" ")
            }
            element.children?.forEach {
                putInTailwind(it, page)
            }
        }

        internal fun processTailwind(page: Page, router: Router) {
            val attributes = mutableSetOf<String>()
            val content = page.content
            if (content != null) {
                if (content.attributes.containsKey(AttributeNames.CLASS)) {
                    putInTailwind(content, page)
                }
                content.children?.forEach {
                    putInTailwind(it, page)
                }
            }
            page.classAttributes.forEach { (_, attribute) ->
                attribute.forEach {
                    if (it.contains(":")) {
                        attributes.add(".${it.substringBefore(":")}\\:${it.substringAfter(":")}:${it.substringBefore(":")}")
                    } else {
                        attributes.add(".$it")
                    }
                }
            }

            val resourceFile = object {}.javaClass.getResource("/input.css")
                ?.let { File(it.toURI()) }
                ?: throw IllegalStateException("Could not find elements.json resource file")

            val fContent = Files.readAllLines(resourceFile.toPath())
            var currentLine = ""
            val processedLines = mutableListOf<String>()

            fContent.forEach { line ->
                if (!line.endsWith("}")) {
                    currentLine += line
                } else {
                    processedLines.add("$currentLine    }")
                    currentLine = ""  // Reset currentLine after processing
                }
            }
            // Don't forget to add the last line if it doesn't end with "},":
            if (currentLine.isNotEmpty()) {
                processedLines.add(currentLine)
            }

            val newProcessedLines = processedLines.filter { line ->
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

            val uuid = UUID.randomUUID()
            val css = newProcessedLines.joinToString().filter { it != ',' }
            router.addRoute(CssPage(uuid, css))
            router.styles[page.target] = uuid to css
        }
    }
}