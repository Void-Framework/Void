package io.void.generator

import io.void.api.CssPage
import io.void.html.Element
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.metadata
import io.void.router.Router
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

object TailwindGen {
    private var resourceFile: String = ""
    private val client =
        HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build()

    /**
     * Fetch tailwind css and store raw content. Call once at startup or when you want to refresh.
     */
    internal fun grabTailwind() {
        val request =
            HttpRequest
                .newBuilder()
                .uri(URI.create("https://cdn.jsdelivr.net/npm/tailwindcss@latest/dist/tailwind.min.css"))
                .GET()
                .build()
        val cResponse = client.send(request, HttpResponse.BodyHandlers.ofString())
        resourceFile = cResponse.body()
    }

    /**
     * Recursively collect classes found on element trees into page.classAttributes (keeps your original method).
     */
    private fun putInTailwind(element: Element, page: Page<*>) {
        if (element.attributes.containsKey("class")) {
            page.classAttributes[element] = element.attributes["class"].split("\\s+".toRegex())
        }
        element.children?.forEach {
            putInTailwind(it, page)
        }
    }

    private fun handleElements(element: Element, page: Page<ContentType.HtmlElements>) {
        // reuse putInTailwind to populate page.classAttributes
        putInTailwind(element, page)
        element.children?.forEach { child ->
            putInTailwind(child, page)
        }
    }

    /**
     * Normalize and escape classes to the form they appear in the tailwind CSS.
     * e.g. "hover:bg-red-500" -> ".hover\:bg-red-500"
     */
    private fun normalizeClassToSelector(className: String): String {
        // basic escapes that Tailwind uses in CSS: colon and slash become backslash-escaped
        // also escape other characters commonly used in class tokens that may appear escaped in compiled CSS
        val escaped = className
            .replace(":", "\\:")
            .replace("/", "\\/")
            .replace(".", "\\.")
            .replace("[", "\\[")
            .replace("]", "\\]")
        return ".$escaped"
    }

    /**
     * Extract selectors that match any of the used class selectors.
     * This uses two passes: non-media top-level rules, and media blocks.
     */
    private fun extractUsedCssBlocks(rawCss: String, usedClassSelectors: Set<String>): String {
        val sb = StringBuilder()

        // 1) include some global rules always (html, body, *, ::before, ::after)
        val globalRegex = Regex("""(^|[\s,])(?:html|body|\*|::before|::after)[^{]*\{[^}]*\}""", RegexOption.DOT_MATCHES_ALL)
        for (m in globalRegex.findAll(rawCss)) {
            sb.append(m.value).append("\n")
        }

        // 2) non-media rules (selectors with a single declaration block)
        // This will match selector blocks that are not part of an @media at the top-level.
        val ruleRegex = Regex("""([^{@][^{}]*?)\{([^{}]*?)\}""", RegexOption.DOT_MATCHES_ALL)
        for (m in ruleRegex.findAll(rawCss)) {
            val selectorBlock = m.groupValues[1].trim()
            val selectors = selectorBlock.split(",").map { it.trim() }

            if (selectors.any { usedClassSelectors.contains(it) }) {
                sb.append(m.value).append("\n")
            }
        }


        // 3) media query blocks — include whole block if any used selector appears inside it
        val mediaRegex = Regex("""@media[^{]+\{(?:(?:[^{}]|\{[^{}]*\})*)\}""", RegexOption.DOT_MATCHES_ALL)
        for (m in mediaRegex.findAll(rawCss)) {
            val mediaBlock = m.value
            if (usedClassSelectors.any { mediaBlock.contains(it) }) {
                sb.append(mediaBlock).append("\n")
            }
        }

        return sb.toString()
    }

    /**
     * Produce the set of class selectors we should scan for in the CSS.
     * E.g. "border-gray-400" -> ".border-gray-400"
     *       "hover:bg-red-500" -> ".hover\:bg-red-500"
     */
    private fun collectClassSelectors(page: Page<ContentType.HtmlElements>): Set<String> {
        val classes = mutableSetOf<String>()
        page.classAttributes.forEach { (_, list) ->
            list.forEach { raw ->
                val trimmed = raw.trim()
                if (trimmed.isNotEmpty()) {
                    classes.add(normalizeClassToSelector(trimmed))
                }
            }
        }
        return classes
    }

    private fun handleMetadataAdding(page: Page<ContentType.HtmlElements>, uuid: UUID) {
        if (page.metadata == null) {
            page.metadata = metadata(page) {
                style = uuid
            }
        } else {
            page.metadata!!.style = uuid
        }
    }

    /**
     * Main entry: gather classes from page, extract only matching Tailwind blocks, register route.
     */
    internal fun processTailwind(page: Page<ContentType.HtmlElements>, router: Router) {
        if (resourceFile.isBlank()) {
            // if not fetched yet, attempt to fetch — keep it simple
            try {
                grabTailwind()
            } catch (e: Exception) {
                // fail silently but return so we don't crash the server
                return
            }
        }

        // populate page.classAttributes map
        handleElements(page.content().htmlElement, page)

        val classSelectors = collectClassSelectors(page)
        if (classSelectors.isEmpty()) {
            // nothing used — still assign empty css to avoid missing route behavior
            val uuid = UUID.randomUUID()
            router.addRoute(CssPage(uuid, ""))
            handleMetadataAdding(page, uuid)
            return
        }

        // Extract used blocks (base rules + media queries)
        val finalCss = extractUsedCssBlocks(resourceFile, classSelectors)

        // Generate UUID, register route, and attach to page metadata
        val uuid = UUID.randomUUID()
        router.addRoute(CssPage(uuid, finalCss))
        handleMetadataAdding(page, uuid)
    }
}

fun <T> List<Pair<T, *>>.containsKey(key: T): Boolean = any { it.first == key }

operator fun <N, M> List<Pair<N, M>>.get(key: N): M {
    return first { it.first == key }.second
}
