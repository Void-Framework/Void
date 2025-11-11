package io.void.generator

import io.void.api.CssPage
import io.void.generator.TailwindGen.grabTailwind
import io.void.generator.TailwindGen.processTailwind
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
import kotlin.reflect.KProperty

/**
 * Extracts only the Tailwind CSS rules actually used by a page and serves them as a scoped stylesheet.
 *
 * At startup [grabTailwind] downloads the canonical Tailwind CSS. On each HTML page render,
 * [processTailwind] walks the element tree to collect class names, extracts matching CSS rules, and
 * registers a unique [CssPage] with the router while wiring it into page metadata.
 */
object TailwindGen {
    private var resourceFile: String = ""
    private val client =
        HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build()

    // Tailwind default breakpoints (v3)
    private val breakpoints =
        mapOf(
            "sm" to "(min-width: 640px)",
            "md" to "(min-width: 768px)",
            "lg" to "(min-width: 1024px)",
            "xl" to "(min-width: 1280px)",
            "2xl" to "(min-width: 1536px)",
        )

    // Supported state variants
    private val statePseudos =
        mapOf(
            "hover" to ":hover",
            "focus" to ":focus",
            "active" to ":active",
        )

    /**
     * Fetches Tailwind CSS and stores its raw content. Called at startup or on demand to refresh.
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

    private fun handleElements(
        element: Element,
        page: Page<ContentType.HtmlElements>,
    ) {
        // reuse putInTailwind to populate page.classAttributes
        if (element.attributes.containsKey("class")) {
            page.classAttributes.addAll(element.attributes["class"].split("\\s+".toRegex()))
        }
        element.children?.forEach {
            handleElements(it, page)
        }
    }

    /**
     * Normalize and escape classes to the form they appear in the tailwind CSS.
     * e.g. "hover:bg-red-500" -> ".hover\:bg-red-500"
     */
    private fun normalizeClassToSelector(className: String): String {
        // basic escapes that Tailwind uses in CSS: colon and slash become backslash-escaped
        // also escape other characters commonly used in class tokens that may appear escaped in compiled CSS
        val escaped =
            className
                .replace(":", "\\:")
                .replace("/", "\\/")
                .replace(".", "\\.")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace(",", "\\,")
        return ".$escaped"
    }

    /**
     * Extract selectors that match any of the used class selectors.
     * This uses two passes: non-media top-level rules, and media blocks.
     */
    private fun extractUsedCssBlocks(usedClassSelectors: Set<String>): String {
        val sb = StringBuilder()

        // 1) include some global rules always (html, body, *, ::before, ::after)
        val globalRegex =
            Regex("""(^|[\s,])(?:html|body|\*|::before|::after)[^{]*\{[^}]*\}""", RegexOption.DOT_MATCHES_ALL)
        for (m in globalRegex.findAll(resourceFile)) {
            sb.append(m.value).append("\n")
        }

        // 2) non-media rules (selectors with a single declaration block)
        // This will match selector blocks that are not part of an @media at the top-level.
        val ruleRegex = Regex("""([^{@][^{}]*?)\{([^{}]*?)\}""", RegexOption.DOT_MATCHES_ALL)
        for (m in ruleRegex.findAll(resourceFile)) {
            val selectorBlock = m.groupValues[1].trim()
            val selectors = selectorBlock.split(",").map { it.trim() }

            // match either exact selector or selector that starts with the class and then a pseudo, e.g.
            // used ".hover\\:underline" matches CSS ".hover\\:underline:hover"
            if (selectors.any { sel -> usedClassSelectors.any { used -> sel == used || sel.startsWith("${'$'}used:") } }) {
                sb.append(m.value).append("\n")
            }
        }

        // 3) media query blocks — include whole block if any used selector appears inside it
        val mediaRegex = Regex("""@media[^{]+\{(?:(?:[^{}]|\{[^{}]*\})*)\}""", RegexOption.DOT_MATCHES_ALL)
        for (m in mediaRegex.findAll(resourceFile)) {
            val mediaBlock = m.value
            if (usedClassSelectors.any { mediaBlock.contains(it) }) {
                sb.append(mediaBlock).append("\n")
            }
        }

        return sb.toString()
    }

    /**
     * Parse a raw class like "sm:hover:mb-[7px]" and, if it is an arbitrary value utility we support,
     * generate the corresponding CSS block text. Currently supports margin/padding utilities with
     * sides and axes (m, mt, mr, mb, ml, mx, my, p, pt, pr, pb, pl, px, py).
     * Returns null if not recognized.
     */
    private fun generateArbitraryUtilityCss(rawClass: String): String? {
        // Split variant prefixes (e.g., sm:hover:)
        val parts = rawClass.split(":")
        if (parts.isEmpty()) return null
        val base = parts.last()
        val prefixes = if (parts.size > 1) parts.dropLast(1) else emptyList()

        // Detect state and responsive variants from prefixes
        val state = prefixes.firstOrNull { statePseudos.containsKey(it) }
        val media = prefixes.firstOrNull { breakpoints.containsKey(it) }

        // Support optional leading '-' for negative values
        val neg = base.startsWith("-")
        val baseNoNeg = if (neg) base.substring(1) else base

        // Match arbitrary m*/p* utilities
        val m = Regex("^([mp][trblxy]?)-\\[(.+)]").matchEntire(baseNoNeg) ?: return null
        val key = m.groupValues[1] // e.g., mb, mx, p, pt
        val valueRaw = m.groupValues[2] // e.g., 7px, 1rem, 10%
        val value = if (neg && !valueRaw.startsWith("-")) "-$valueRaw" else valueRaw

        val (properties, shorthand) =
            when (key[0]) {
                'm' -> mapMarginPaddingProperties('m', key)
                'p' -> mapMarginPaddingProperties('p', key)
                else -> return null
            }

        val selectorBase = normalizeClassToSelector(rawClass)
        val pseudo = state?.let { statePseudos[it] } ?: ""
        val selector = selectorBase + pseudo

        val declarations =
            buildString {
                if (shorthand != null) {
                    append("$shorthand: $value;")
                } else {
                    properties.forEachIndexed { idx, prop ->
                        if (idx > 0) append(' ')
                        append("$prop: $value;")
                    }
                }
            }

        val rule = "$selector{$declarations}"
        return if (media != null) {
            val mq = breakpoints[media]
            "@media $mq{$rule}"
        } else {
            rule
        }
    }

    private fun mapMarginPaddingProperties(
        kind: Char,
        key: String,
    ): Pair<List<String>, String?> {
        val baseProp = if (kind == 'm') "margin" else "padding"
        return when (key) {
            "m", "p" -> emptyList<String>() to baseProp // shorthand property
            "mt", "pt" -> listOf("$baseProp-top") to null
            "mr", "pr" -> listOf("$baseProp-right") to null
            "mb", "pb" -> listOf("$baseProp-bottom") to null
            "ml", "pl" -> listOf("$baseProp-left") to null
            "mx", "px" -> listOf("$baseProp-left", "$baseProp-right") to null
            "my", "py" -> listOf("$baseProp-top", "$baseProp-bottom") to null
            else -> emptyList<String>() to null
        }
    }

    /**
     * Iterate over all raw classes and synthesize CSS for supported arbitrary utilities.
     */
    private fun generateSyntheticCssForArbitrary(rawClasses: Set<String>): String {
        val sb = StringBuilder()
        val seen = mutableSetOf<String>()
        rawClasses.forEach { cls ->
            // quickly filter for bracket utilities to avoid extra work
            if (!cls.contains('[') || !cls.contains(']')) return@forEach
            val css = generateArbitraryUtilityCss(cls)
            if (css != null && seen.add(css)) {
                sb.append(css).append('\n')
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
        page.classAttributes.forEach { raw ->
            val trimmed = raw.trim()
            if (trimmed.isNotEmpty()) {
                classes.add(normalizeClassToSelector(trimmed))
            }
        }
        return classes
    }

    /**
     * Collect raw class tokens as they appear on elements (without normalization). Useful for
     * generating synthetic CSS for arbitrary values not present in the CDN build.
     */
    private fun collectRawClasses(page: Page<ContentType.HtmlElements>): Set<String> {
        val classes = mutableSetOf<String>()
        page.classAttributes.forEach { raw ->
            val trimmed = raw.trim()
            if (trimmed.isNotEmpty()) {
                classes.add(trimmed)
            }
        }
        return classes
    }

    private fun handleMetadataAdding(
        page: Page<ContentType.HtmlElements>,
        uuid: UUID,
    ) {
        if (page.metadata == null) {
            page.metadata =
                metadata(page) {
                    style = uuid
                }
        } else {
            page.metadata!!.style = uuid
        }
    }

    /**
     * Main entry: gather classes from page, extract only matching Tailwind blocks, register route.
     */
    internal fun processTailwind(
        page: Page<ContentType.HtmlElements>,
        router: Router,
    ) {
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
            return
        }

        // Extract used blocks (base rules + media queries)
        var finalCss = extractUsedCssBlocks(classSelectors)

        // Generate synthetic CSS for arbitrary value utilities (e.g., mb-[7px]) including variants
        val rawClasses = collectRawClasses(page)
        val synthetic = generateSyntheticCssForArbitrary(rawClasses)
        if (synthetic.isNotBlank()) {
            finalCss += "\n" + synthetic
        }

        // Generate UUID, register route, and attach to page metadata
        val uuid = UUID.randomUUID()
        router.addRoute(CssPage(uuid, finalCss))
        handleMetadataAdding(page, uuid)
    }
}

/**
 * Returns true if any pair in this list has its first component equal to [key].
 *
 * Useful for treating a List of Pair as a lightweight map in small utilities.
 * Runs in O(n).
 */
fun <T> List<Pair<T, *>>.containsKey(key: T): Boolean = any { it.first == key }

/**
 * Retrieves the second component for the first pair whose first component equals [key].
 */
operator fun <N, M> List<Pair<N, M>>.get(key: N): M = first { it.first == key }.second


data class TailwindString(
    val classes: String
) {

    operator fun provideDelegate(ref: Page<ContentType.HtmlElements>, property: KProperty<*>): String {
        ref.classAttributes.addAll(classes.split("\\s+".toRegex()))
        return classes
    }
}
