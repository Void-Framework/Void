package io.void.generator

import io.void.generator.exception.NoOutputException
import java.io.File
import java.nio.file.Files

fun main(args: Array<String>) {
    val output: File
    if (args.isNotEmpty()) {
        if (args.first() == "--output") {
            output = File(args[1])
        } else {
            throw NoOutputException()
        }
    } else {
        throw NoOutputException()
    }
    val resourceFile =
        object {}
            .javaClass
            .getResource("/elements.json")
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
            currentLine = "" // Reset currentLine after processing
        }
    }
    // Don't forget to add the last line if it doesn't end with "},":
    if (currentLine.isNotEmpty()) {
        processedLines.add(currentLine)
    }

    val codeFiles = processLinesToCodeFiles(processedLines)
    makeFiles(output, codeFiles)
}

fun makeFiles(
    parent: File,
    content: MutableMap<String, String>,
) {
    if (!File(parent.path).exists()) {
        File(parent.path).mkdir()
    }
    content.forEach { (name, code) ->
        val newFile = File("${parent.path}${File.separator}$name.kt")
        Files.createFile(newFile.toPath())
        Files.write(newFile.toPath(), code.toByteArray())
    }
}

fun processLinesToCodeFiles(lines: MutableList<String>): MutableMap<String, String> {
    val codeFiles = mutableMapOf<String, String>()

    lines.forEach { line ->
        val kotlinCode =
            StringBuilder(
                "package io.void.generated\n\nimport io.void.html.attributes.Attribute\nimport io.void.html.attributes.AttributeNames\nimport io.void.generated.*\nimport kotlin.reflect.KClass\n",
            )
        val startLength = kotlinCode.length
        val attributeBuilder = StringBuilder("")
        val name = line.substringBefore("\":").substringAfter("\"").capitalize()
        val type = line.substringAfter("\"contentModel\": \"").substringBefore("\"").capitalize()
        val attributes =
            line
                .substringAfter(
                    "\"attributes\": [",
                ).substringBefore("]")
                .split(", ")
                .map { it.replace("\"", "").uppercase() }
                .toList()
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
                val HElement = name.startsWith("h", true) && name[name.length - 1].digitToIntOrNull() != null
                kotlinCode.append(
                    "\nclass $name(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = \"${name.lowercase()}\")${if (HElement) {
                        ", HElement"
                    } else {
                        ""
                    }
                    } {\n",
                )
                kotlinCode.insert(
                    startLength,
                    "import io.void.html.Element\nimport io.void.html.ElementWithChildren\n${if (HElement) {
                        "import io.void.html.HElement\n"
                    } else {
                        ""
                    }
                    }",
                )
                val childrenBuilder = StringBuilder("null")
                val acceptedChildren =
                    line
                        .substringAfter(
                            "\"acceptedChildren\": [",
                        ).substringBefore("]")
                        .split(", ")
                        .map { it.replace("\"", "") }
                        .toList()
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
                kotlinCode.append(
                    "    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf($childrenBuilder)\n",
                )
            }
            "Void" -> {
                kotlinCode.append("\nclass $name(vararg attributes: Attribute): SelfClosingElement(\"${name.lowercase()}\") {\n")
                kotlinCode.insert(startLength, "import io.void.html.SelfClosingElement\nimport io.void.html.Element\n")
            }
            else -> throw UnsupportedOperationException()
        }
        kotlinCode.append("    override val allowedAttributes: List<AttributeNames> = listOf($attributeBuilder)\n\n")

        var extension: String = ""
        when (type) {
            "Normal" -> {
                kotlinCode.append("    init {\n        this.apply(function)\n        addAttributes(*attributes)\n    }\n\n")
                extension =
                    "    fun Element.${name.capitalize()}(vararg attribute: Attribute, _children: Element.() -> Unit): ${name.capitalize()} {\n" +
                    "        val ${name.capitalize()} = ${name.capitalize()}(\n            attributes = attribute,\n            function = _children\n        )\n" +
                    "        children!!.add(${name.capitalize()})\n        return ${name.capitalize()}\n    }\n"
            }
            "Void" -> {
                kotlinCode.append("\n    init {\n        addAttributes(*attributes)\n    }\n\n")
                extension = "    fun Element.${name.capitalize()}(vararg attribute: Attribute): ${name.capitalize()} {\n" +
                    "        val ${name.capitalize()} = ${name.capitalize()}(\n            attributes = attribute\n        )\n" +
                    "        children!!.add(${name.capitalize()})\n        return ${name.capitalize()}\n    }\n"
            }
        }

        kotlinCode.append("}")
        kotlinCode.append(extension)
        codeFiles[name] = kotlinCode.toString()
    }

    return codeFiles
}

fun String.capitalize(): String =
    this.replaceFirstChar {
        it.uppercase()
    }
