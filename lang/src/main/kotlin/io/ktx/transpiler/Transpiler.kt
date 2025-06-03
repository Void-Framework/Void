package io.ktx.transpiler

import java.io.BufferedReader
import java.io.File
import java.util.UUID

class Transpiler(val file: File) {

    private val functionPattern = Regex("""fun\s+Page\.(\w+)\s*\([^)]*\)\s*(?::\s*[\w\d_<>,\s?]+)?\s*\{""")
    private val routePattern = Regex("this\\.route\\s*=\\s*\"(.+?)\"")
    private val startFractalPattern = Regex("""<~>""")
    private val endFractalPattern = Regex("""<~/>""")
    private val inlineCall = Regex("\\|(.*?)\\|")
    private var uuid = UUID.randomUUID()
    private var route = "" to uuid
    private val replacementString = "${'$'}{$1}"
    private var insideFunction = false
    private var insideHTML = false

    fun transpile(): Map<String, String> {
        val content = BufferedReader(file.reader())
        val routes = mutableMapOf<String, String>()
        content.forEachLine {
            if (it.matches(functionPattern)) {
                insideFunction = true
            } else if (insideFunction) {
                val html = handleString(it)
                if (!insideHTML && html != null) {
                    routes.put(route.first, html)
                    route = "" to uuid
                }
            }
        }
        return routes
    }

    fun handleString(it: String): String? {
        when {
            it.matches(startFractalPattern) && it.matches(endFractalPattern) -> {
                insideFunction = false
                insideHTML = false
                uuid = UUID.randomUUID()
                return inlineCall.replace(it.substringBefore("<~/>").substringAfter("<~>"), replacementString)
            }
            it.matches(startFractalPattern) -> {
                insideHTML = true
                return null
            }
            it.matches(endFractalPattern) -> {
                insideFunction = false
                insideHTML = false
                uuid = UUID.randomUUID()
                return null
            }
            it.matches(routePattern) -> {
                if (route.first.isEmpty()) {
                    route = routePattern.find(it)?.groups?.get(1)?.value!! to uuid
                    return null
                } else {
                    error("Already defined one route for specified page: ${routePattern.find(it)?.groups?.get(1)?.value!!}")
                }
            }
            else -> {
                if (insideHTML) {
                    return inlineCall.replace(it, replacementString)
                }
            }
        }
        return null
    }
}