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
        val kotlinCode = StringBuilder("package io.void.generated\n\nimport io.void.html.attributes.Attribute\nimport io.void.html.attributes.AttributeNames\nimport io.void.generated.*\nimport kotlin.reflect.KClass\n")
        val startLength = kotlinCode.length
        val attributeBuilder = StringBuilder("")
        val name = line.substringBefore("\":").substringAfter("\"").capitalize()
        val type = line.substringAfter("\"contentModel\": \"").substringBefore("\"").capitalize()
        val attributes = line.substringAfter("\"attributes\": [").substringBefore("]").split(", ").map { it.replace("\"", "").uppercase() }.toList()
        if (attributes.isNotEmpty()) {
            attributes.forEach { attribute ->
                if (attribute.isNotBlank()) {
                    attributeBuilder.append("AttributeNames.$attribute, ")
                }
            }
            if (attributeBuilder.isNotEmpty()) {
                attributeBuilder.setLength(attributeBuilder.length - 2)
            }
        }
        when (type) {
            "Normal" -> {
                kotlinCode.append("\nclass $name(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = \"${name.lowercase()}\") {\n")
                kotlinCode.insert(startLength, "import io.void.html.element.Element\nimport io.void.html.element.ElementWithChildren\n")
                val childrenBuilder = StringBuilder("null")
                val acceptedChildren = line.substringAfter("\"acceptedChildren\": [").substringBefore("]").split(", ").map { it.replace("\"", "") }.toList()
                if (acceptedChildren.isNotEmpty()) {
                    if (acceptedChildren[0].isNotBlank()) {
                        childrenBuilder.setLength(0)
                    }
                    acceptedChildren.forEach { child ->
                        if (child.isNotBlank()) {
                            childrenBuilder.append("${child.capitalize()}::class, ")
                        }
                    }
                    if (acceptedChildren[0].isNotBlank()) {
                        childrenBuilder.setLength(childrenBuilder.length - 2)
                    }
                }
                kotlinCode.append("    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf($childrenBuilder)\n\n")
            }
            "Void" -> {
                kotlinCode.append("\nclass $name(vararg attribute: Attribute): SelfClosingElement(\"${name.lowercase()}\") {\n")
                kotlinCode.insert(startLength, "import io.void.html.element.SelfClosingElement\n")
                kotlinCode.append("\n    init {\n        addAttributes(*attribute)\n    }\n\n")
            }
            else -> throw UnsupportedOperationException()
        }
        kotlinCode.append("    override val allowedAttributes: List<AttributeNames> = listOf($attributeBuilder)\n")

        kotlinCode.append("}")
        println(kotlinCode)
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        it.uppercase()
    }
}