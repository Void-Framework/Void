package io.ktx.transpiler

import java.io.BufferedReader
import java.io.File

class Transpiler(val file: File) {

    private val functionPattern = Regex("""fun\s+Page\.(\w+)\s*\([^)]*\)\s*(?::\s*[\w\d_<>,\s?]+)?\s*\{""")
    private val routePattern = Regex("this\\.route\\s*=\\s*\"(.+?)\"")
    private val startFractalPattern = Regex("""<~>""")
    private val endFractalPattern = Regex("""<~/>""")
    private val inlineCall = Regex("\\|(.*?)\\|")
    private var currentRoute = ""
    private val replacementString = "${'$'}{$1}"
    private var insideFunction = false
    private var insideHTML = false
    private var fractalMatched = false
    private val routes = mutableMapOf<String, String>()
    private val singleLineCommentPattern = Regex("//.*") // New regex for // comments

    fun transpile(): Map<String, String> {
        val content = BufferedReader(file.reader())
        val html = StringBuilder("")
        content.forEachLine {
            if (it.matches(functionPattern)) {
                insideFunction = true
            } else if (insideFunction) {
                if (insideHTML) {
                    html.append(handleString(it) ?: "")
                }
                if (!insideHTML && fractalMatched) {
                    routes.put(currentRoute, html.toString())
                    currentRoute = ""
                    html.delete(0, html.length)
                    fractalMatched = false
                }
            }
        }
        return routes
    }

    fun handleString(it: String): String? {
        val cleanedLine = singleLineCommentPattern.replace(it, "").trim()
        when {
            cleanedLine.matches(startFractalPattern) && cleanedLine.matches(endFractalPattern) -> {
                insideFunction = false
                insideHTML = false
                routes.put(currentRoute, inlineCall.replace(cleanedLine.substringBefore("<~/>").substringAfter("<~>"), replacementString))
                currentRoute = ""
                return null
            }
            cleanedLine.matches(startFractalPattern) -> {
                fractalMatched = true
                insideHTML = true
                return null
            }
            cleanedLine.matches(endFractalPattern) -> {
                insideFunction = false
                insideHTML = false
                return null
            }
            cleanedLine.matches(routePattern) -> {
                if (currentRoute.isEmpty()) {
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