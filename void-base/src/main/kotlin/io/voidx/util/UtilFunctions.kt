package io.voidx.util

/** Wraps this value in a successful [Result]. */
fun <T> T.toResult(): Result<T> = Result.success(this)

/** Wraps this exception in a failed [Result]. */
fun <T> Exception.toResult(): Result<T> = Result.failure(this)

/**
 * Reads the classpath resource at [path] using the class loader of [clazz].
 * Useful when loading resources packaged alongside a specific class.
 */
fun readResourceText(
    path: String,
    clazz: Class<*>,
): String =
    clazz
        .getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")

/**
 * Reads the classpath resource at [path] using the thread context class loader.
 */
fun readResourceText(path: String): String =
    Thread
        .currentThread()
        .contextClassLoader
        .getResourceAsStream(path)
        ?.bufferedReader()
        ?.use { it.readText() }
        ?: error("Missing resource: $path")

/**
 * Removes a single trailing empty string element from this list if present.
 * Useful when splitting URL paths to ignore a trailing slash.
 *
 * @return true if an empty element was removed; false otherwise.
 */
fun MutableList<String>.trimTrailingEmpty(): Boolean {
    val hasEmptyTail = this.lastOrNull()?.isEmpty() == true
    if (hasEmptyTail) removeAt(lastIndex)
    return hasEmptyTail
}
