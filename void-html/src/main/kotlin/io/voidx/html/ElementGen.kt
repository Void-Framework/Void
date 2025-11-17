package io.voidx.html

import io.voidx.html.exception.NoOutputException
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
    val stream = object {}.javaClass.getResourceAsStream("/elements.json")
        ?: error("Could not find elements.json")

    val temp = kotlin.io.path.createTempFile("elements", ".json").toFile()
    temp.outputStream().use { out ->
        stream.copyTo(out)
    }

    val resourceFile: File = temp

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
        val kotlinCode = StringBuilder(
            "package io.voidx.html.generated\n\n" +
                    "import io.voidx.html.*\n" +
                    "import kotlin.reflect.KClass\n\n"
        )

        val name = line.substringBefore("\":").substringAfter("\"").capitalize()
        val tagName = name.lowercase()
        val type = line.substringAfter("\"contentModel\": \"").substringBefore("\"").capitalize()
        when (type) {
            "Normal" -> {
                // Generate a composable extension that creates an ElementWithChildren instance (anonymous subclass)
                kotlinCode.append(
                    """
                    fun Element.$name(vararg attribute: Attribute, child: Element.() -> Unit): ElementWithChildren {
                        val node = object : ElementWithChildren(name = "$tagName") {
                                override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
                            }
                        // apply attributes (remembered instance will keep attributes across recompositions)
                        node.addAttributes(*attribute)
                        // append to parent
                        children!!.add(node)
                        node.child()
                        return node
                    }
                    
                    """.trimIndent()
                )
            }

            "Void" -> {
                // Generate a composable extension that creates a SelfClosingElement instance (anonymous subclass)
                kotlinCode.append(
                    """
                    fun Element.$name(vararg attribute: Attribute): SelfClosingElement {
                        val node = object : SelfClosingElement("$tagName") {}
                        node.addAttributes(*attribute)
                        children!!.add(node)
                        return node
                    }
                    
                    """.trimIndent()
                )
            }
            else -> throw UnsupportedOperationException("Unknown contentModel: $type")
        }

        codeFiles[name] = kotlinCode.toString()
    }

    return codeFiles
}


fun String.capitalize(): String =
    this.replaceFirstChar {
        it.uppercase()
    }
