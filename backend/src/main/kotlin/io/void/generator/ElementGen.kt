package io.void.generator

import io.void.html.*
import androidx.compose.runtime.*
import kotlin.reflect.KClass
import java.io.File
import java.nio.file.Files
import io.void.generator.exception.NoOutputException

fun main(args: Array<String>) {
    val output: File = if (args.isNotEmpty() && args.first() == "--output") File(args[1]) else throw NoOutputException()

    val resourceFile = object {}.javaClass.getResource("/elements.json")?.let { File(it.toURI()) } ?: throw IllegalStateException("Could not find elements.json resource file")
    val content = Files.readAllLines(resourceFile.toPath())

    val processedLines = mutableListOf<String>()
    var currentLine = ""
    content.forEach { line ->
        currentLine += line
        if (line.endsWith("},")) {
            processedLines.add(currentLine)
            currentLine = ""
        }
    }
    if (currentLine.isNotEmpty()) processedLines.add(currentLine)

    val codeFiles = processLinesToCodeFiles(processedLines)
    makeFiles(output, codeFiles)
}

fun makeFiles(parent: File, content: MutableMap<String, String>) {
    if (!parent.exists()) parent.mkdirs()
    content.forEach { (name, code) ->
        val newFile = File(parent, "$name.kt")
        Files.write(newFile.toPath(), code.toByteArray())
    }
}

fun processLinesToCodeFiles(lines: MutableList<String>): MutableMap<String, String> {
    val codeFiles = mutableMapOf<String, String>()

    lines.forEach { line ->
        val kotlinCode = StringBuilder(
            "package io.void.generated\n\n" +
                    "import io.void.html.*\n" +
                    "import androidx.compose.runtime.*\n" +
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
                    @Composable
                    fun Element.$name(vararg attribute: Attribute, _children: @Composable Element.() -> Unit) {
                        val node = remember {
                            object : ElementWithChildren(name = "$tagName") {
                                // Accept-any-children for now (see notes)
                                override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
                            }
                        }
                        // apply attributes (remembered instance will keep attributes across recompositions)
                        node.addAttributes(*attribute)
                        // append to parent
                        children!!.add(node)
                        with(node) { _children() }
                    }
                    
                    """.trimIndent()
                )
            }

            "Void" -> {
                // Generate a composable extension that creates a SelfClosingElement instance (anonymous subclass)
                kotlinCode.append(
                    """
                    @Composable
                    fun Element.$name(vararg attribute: Attribute) {
                        val node = remember {
                            object : SelfClosingElement("$tagName") {}
                        }
                        node.addAttributes(*attribute)
                        children!!.add(node)
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


fun String.capitalize(): String = this.replaceFirstChar { it.uppercase() }