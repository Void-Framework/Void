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
        val name = line.substringBefore("\":").substringAfter("\"").capitalize()
        val type = line.substringAfter("\"contentModel\": \"").substringBefore("\"").capitalize()
        println("$name, it's type is $type")
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar {
        it.uppercase()
    }
}