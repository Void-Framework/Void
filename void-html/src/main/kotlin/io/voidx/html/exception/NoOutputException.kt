package io.voidx.html.exception

/**
 * Thrown when a required output directory/path has not been configured for the generator.
 *
 * Indicates that code attempted to write generated assets without a destination. Configure
 * an output path before invoking the generator to avoid this exception.
 */
class NoOutputException : Exception("No output directory was specified.")
