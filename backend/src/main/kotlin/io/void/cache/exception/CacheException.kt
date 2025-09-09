package io.void.cache.exception

internal class CacheException(e: Exception): Exception("Cache failed with the following exception: ${e.stackTrace.joinToString("\n")}") {
}