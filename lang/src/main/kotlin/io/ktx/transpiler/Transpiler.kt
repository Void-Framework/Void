package io.ktx.transpiler

import java.io.BufferedReader
import java.io.File

class Transpiler(val file: File) {

    private val functionPattern = Regex("""fun\s+Page\.(\w+)\s*\([^)]*\)\s*(?::\s*[\w\d_<>,\s?]+)?\s*\{""")
    private val routePattern = Regex("this\\.route\\s*=\\s*\"(.+?)\"")
    private val startFractalPattern = Regex("<~>")
    private val endFractalPattern = Regex("<~/>")
    private val inlineCall = Regex("\\|(.*?)\\|")
    private var currentRoute = ""
    private val replacementString = "${'$'}{$1}"
    private var insideFunction = false
    private var insideHTML = false
    private var fractalMatched = false
    private val routes = mutableMapOf<String, String>()
    private val singleLineCommentPattern = Regex("//.*") // New regex for // comments
    private var closingTag = false
    private var routeDefined = false
    private var functionName = ""

    fun transpile(): Map<String, String> {
        val content = BufferedReader(file.reader())
        val html = StringBuilder("")
        content.forEachLine {
            if (it.matches(functionPattern)) {
                handleFractalErrors()
                functionName = functionPattern.find(it)?.groups?.get(1)?.value!!
                insideFunction = true
            } else if (insideFunction) {
                val text = handleString(it) ?: ""
                if (insideHTML) {
                    html.append(text)
                }
                if (!insideHTML && fractalMatched) {
                    routes.put(currentRoute, html.toString())
                    currentRoute = ""
                    html.delete(0, html.length)
                    fractalMatched = false
                    closingTag = false
                }
            }
        }
        handleFractalErrors()
        return routes
    }

    private fun handleFractalErrors() {
        if (insideFunction && insideHTML) {
            error("Fractal tag hasn't been closed at route: $currentRoute")
        } else if (insideFunction && !fractalMatched && closingTag) {
            error("Fractal tag not opened at route: $currentRoute")
        } else if (insideHTML && !routeDefined) {
            error("No route defined at function: $functionName")
        }
    }

    private fun handleString(it: String): String? {
        val cleanedLine = singleLineCommentPattern.replace(it, "").trim()
        when {
            cleanedLine.contains(startFractalPattern) && cleanedLine.contains(endFractalPattern) -> {
                insideFunction = false
                insideHTML = false
                routes.put(currentRoute, inlineCall.replace(cleanedLine.substringBefore("<~/>").substringAfter("<~>"), replacementString))
                currentRoute = ""
                return null
            }
            cleanedLine.contains(startFractalPattern) -> {
                fractalMatched = true
                insideHTML = true
                return null
            }
            cleanedLine.contains(endFractalPattern) -> {
                insideFunction = false
                insideHTML = false
                closingTag = true
                return null
            }
            cleanedLine.matches(routePattern) -> {
                if (currentRoute.isEmpty()) {
                    routeDefined = true
                    currentRoute = routePattern.find(cleanedLine)?.groups?.get(1)?.value!!
                    return null
                } else {
                    error("Already defined one route for specified page: ${routePattern.find(cleanedLine)?.groups?.get(1)?.value!!}")
                }
            }
            else -> {
                if (insideHTML) {
                    return inlineCall.replace(cleanedLine, replacementString)
                }
            }
        }
        return null
    }
}