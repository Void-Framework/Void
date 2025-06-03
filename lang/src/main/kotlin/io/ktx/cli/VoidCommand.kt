package io.ktx.cli

import io.ktx.transpiler.Transpiler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
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
            scope.launch {
                handleAddingRoutes(it)
            }
        } else {
            if (it.extension == "ktx") {
                scope.launch {
                    handleTranspiling(it)
                }
            }
        }
    }
}

fun handleTranspiling(file: File) {
    val transpiler = Transpiler(file)
    val map = transpiler.transpile()
}
