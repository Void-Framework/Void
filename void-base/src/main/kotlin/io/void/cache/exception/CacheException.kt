package io.void.cache.exception

/**
 * Wrapper exception thrown when page caching fails.
 *
 * Used by the cache processor to surface the underlying [Exception] that occurred
 * while rendering a page or refreshing its cached response.
 */
internal class CacheException(
    e: Exception,
) : Exception("Cache failed with the following exception: ${e.stackTrace.joinToString("\n")}")
