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
        val kotlinCode = StringBuilder("package io.void.generated\n\nimport io.void.html.*\nimport androidx.compose.runtime.*\nimport kotlin.reflect.KClass\n")
        val name = line.substringBefore("\":").substringAfter("\"").capitalize()
        val type = line.substringAfter("\"contentModel\": \"").substringBefore("\"").capitalize()

        when(type) {
            "Normal" -> {
                kotlinCode.append("\nclass $name(vararg attributes: Attribute, function: Element.() -> Unit) : ElementWithChildren(name = \"${name.lowercase()}\") {\n")
                kotlinCode.append("    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)\n")
                kotlinCode.append("    init { this.apply(function); addAttributes(*attributes) }\n}\n")

                // Compose DSL wrapper
                kotlinCode.append("@Composable\n")
                kotlinCode.append("fun Element.$name(vararg attribute: Attribute, _children: @Composable Element.() -> Unit): $name {\n")
                kotlinCode.append("    val node = $name(attributes = attribute) {\n")
                kotlinCode.append("        Fractal(_children)\n")
                kotlinCode.append("    }\n")
                kotlinCode.append("    children!!.add(node)\n")
                kotlinCode.append("    return node\n")
                kotlinCode.append("}\n")

            }

            "Void" -> {
                kotlinCode.append("\nclass $name(vararg attributes: Attribute) : SelfClosingElement(\"${name.lowercase()}\") { init { addAttributes(*attributes) } }\n")

                // Compose DSL wrapper
                kotlinCode.append("\n@Composable\nfun Element.$name(vararg attribute: Attribute): $name {\n")
                kotlinCode.append("    val node = $name(attributes = attribute)\n    children!!.add(node)\n    return node\n}\n")
            }

            else -> throw UnsupportedOperationException()
        }

        codeFiles[name] = kotlinCode.toString()
    }

    return codeFiles
}

fun String.capitalize(): String = this.replaceFirstChar { it.uppercase() }
