package io.void.generator

import io.void.generator.exception.NoOutputException
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files

fun main(args: Array<String>) {
    /*val output: File
    if (args.first() == "--output") {
        output = File(args[1])
    } else {
        throw NoOutputException()
    }*/
    val resourceFile = object {}.javaClass.getResource("/elements.json")
        ?.let { File(it.toURI()) }
        ?: throw IllegalStateException("Could not find elements.json resource file")

    val content = Files.readAllLines(resourceFile.toPath())
    var currentLine = ""
    val processedLines = mutableListOf<String>()

    content.forEach { line ->
        if (!line.endsWith("},")) {
            currentLine += line
        } else {
            processedLines.add(currentLine)
            currentLine = ""  // Reset currentLine after processing
        }
    }
    // Don't forget to add the last line if it doesn't end with "},":
    if (currentLine.isNotEmpty()) {
        processedLines.add(currentLine)
    }

    processedLines.forEach { line ->
        val kotlinCode = StringBuilder("package io.void.generated\n")
        val attributeBuilder = StringBuilder("")
        val name = line.substringBefore("\":").substringAfter("\"").capitalize()
        val type = line.substringAfter("\"contentModel\": \"").substringBefore("\"").capitalize()
        val attributes = line.substringAfter("\"attributes\": [").substringBefore("]").split(", ").map { it.replace("\"", "").uppercase() }.toList()
        if (attributes.isNotEmpty()) {
            attributes.forEach { attribute ->
                if (attribute.isNotBlank()) {
                    attributeBuilder.append("AttributeNames.$attribute,")
                }
            }
            if (attributeBuilder.isNotEmpty()) {
                attributeBuilder.setLength(attributeBuilder.length - 1)
            }
        }
        when (type) {
            "Normal" -> kotlinCode.append("\nclass $name(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = \"${name.lowercase()}\") {\n")
            "Void" -> kotlinCode.append("\nclass $name(vararg attribute: Attribute): SelfClosingElement(\"${name.lowercase()}\") {\n")
            else -> throw UnsupportedOperationException()
        }
        kotlinCode.append("override val allowedAttributes: List<AttributeNames> = listOf($attributeBuilder)\n")

        println(kotlinCode)
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        it.uppercase()
    }
}