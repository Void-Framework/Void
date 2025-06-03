package io.ktx.cli

import io.ktx.transpiler.Transpiler
import java.io.File

fun main(args: Array<String>) {
    val currentDirectoryPath = System.getProperty("user.dir")
    try {
        when (args.first()) {
            "routes" -> handleAddingRoutes(File(currentDirectoryPath))
        }
    } catch (e: Exception) {
        println("No arguments supplied, please refer to the documentation.")
    }
}

fun handleAddingRoutes(folder: File) {
    folder.listFiles()?.forEach {
        if (it.isDirectory) {
            handleAddingRoutes(it)
        } else {
            if (it.extension == "ktx") {
                handleTranspiling(it)
            }
        }
    }
}

fun handleTranspiling(file: File) {
    val transpiler = Transpiler(file)
}
