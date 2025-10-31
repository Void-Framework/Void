package io.void.json

/**
 * Supported payload formats detected from HTTP headers.
 *
 * The mapping used by [io.void.json.detectFormat]:
 * - JSON -> `application/json`
 * - CBOR -> `application/cbor`
 * - XML  -> `application/xml`
 * - TEXT -> any other or missing content type
 */
enum class Format {
    JSON,
    CBOR,
    XML,
    TEXT,
}
