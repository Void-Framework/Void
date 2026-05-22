package io.voidx.json

import io.voidx.dto.RequestDTO
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildResponse
import io.voidx.json.JsonConfigs.default
import io.voidx.page.Page
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.reflect.full.findAnnotation

/**
 * Core JSON/serialization helpers and small HTTP utilities.
 *
 * This file provides:
 * - Shared JSON configurations via [JsonConfigs]
 * - Convenience extensions to serialize/deserialize objects to/from JSON, CBOR, and ProtoBuf
 * - Base64 helpers for JSON payloads
 * - File helpers to persist and load JSON
 * - Small [RequestDTO] helpers like [parseBody] and [detectFormat]
 * - A [autoSerialize] helper to produce a [ResponseDTO] from a value based on the request's Accept header
 */
object JsonConfigs {
    /** Default JSON config: ignores unknown keys and encodes default values. */
    val default =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    /** Pretty-printing JSON config with the same semantics as [default]. */
    val pretty =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
}

/** Serializes this object to a JSON string using either [JsonConfigs.default] or [JsonConfigs.pretty]. */
inline fun <reified T : Any> T.toJson(pretty: Boolean = false): Result<String> =
    runCatching {
        val json = if (pretty) JsonConfigs.pretty else default
        json.encodeToString(this)
    }

/** Deserializes this JSON string into type [T] using the default [kotlinx.serialization.json.Json] instance. */
inline fun <reified T : Any> String.fromJson(): Result<T> = runCatching { Json.decodeFromString(this) }

/** Encodes this object to CBOR bytes using [kotlinx.serialization.cbor.Cbor]. */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.toBytes(): Result<ByteArray> = runCatching { Cbor.encodeToByteArray(this) }

/** Decodes CBOR bytes into type [T] using [kotlinx.serialization.cbor.Cbor]. Note: despite the name, this parses CBOR, not JSON. */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.fromJson(): Result<T> = runCatching { Cbor.decodeFromByteArray(this) }

/** Encodes this object to ProtoBuf bytes using [kotlinx.serialization.protobuf.ProtoBuf]. */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.toXml(): Result<ByteArray> = runCatching { ProtoBuf.encodeToByteArray(this) }

/** Decodes ProtoBuf bytes into type [T] using [kotlinx.serialization.protobuf.ProtoBuf]. */
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.fromXml(): Result<T> = runCatching { ProtoBuf.decodeFromByteArray(this) }

/** Parses the textual [RequestDTO.body] as JSON into type [T]. */
inline fun <reified T> RequestDTO.parseBody(): Result<T> = runCatching { Json.decodeFromString(this.body) }

/** Detects the request body [Format] based on the `Content-Type` header. */
fun RequestDTO.detectFormat(): Format {
    val raw = headers["Content-Type"] ?: return Format.TEXT
    // Extract media type without parameters and lowercase it
    val mediaType = raw.substringBefore(';').trim().lowercase(Locale.getDefault())

    return when {
        mediaType == "application/json" -> Format.JSON

        // Common vendor or structured syntax suffix e.g. application/hal+json
        mediaType.endsWith("+json") || mediaType == "text/json" -> Format.JSON

        mediaType == "application/cbor" -> Format.CBOR

        mediaType == "application/xml" || mediaType == "text/xml" -> Format.XML

        else -> Format.TEXT
    }
}

/**
 * Creates a [ResponseDTO] by serializing [value] according to the request `Accept` header.
 * Defaults to `application/json` when the header is missing.
 *
 * Generic overload preserves the static type [T] so kotlinx.serialization can locate the correct serializer.
 */
inline fun <reified T : Any> Page.autoSerialize(
    request: RequestDTO,
    value: T,
): ResponseDTO {
    val accept = request.headers["Accept"] ?: "application/json"
    return when {
        "application/json" in accept -> {
            buildResponse<String> {
                headers["Content-Type"] = "application/json"
                body = value.toJson<T>().getOrThrow()
            }
        }

        "application/xml" in accept -> {
            buildResponse<ByteArray> {
                headers["Content-Type"] = "application/xml"
                body = value.toXml<T>().getOrThrow()
            }
        }

        else -> {
            buildResponse<String> {
                headers["Content-Type"] = accept
                body = value.toString()
            }
        }
    }
}

/**
 * Backward-compatible overload that accepts [Any]. If the static type is not preserved (no reified generic),
 * JSON serialization cannot be guaranteed; this version falls back to stringification for unknown types.
 */
// Note: A non-generic Any overload is intentionally omitted to avoid JVM signature clashes
// and to ensure kotlinx.serialization serializers can be resolved via the reified type.

/** Writes JSON representation of this object to the given [path]. Creates the file if needed. */
inline fun <reified T : Any> T.toJsonFile(
    pretty: Boolean = false,
    path: Path,
) {
    Files.createFile(path)
    Files.write(path, this.toJson(pretty).getOrNull()!!.toByteArray())
}

/** Reads JSON content from this [File] into type [T]. */
inline fun <reified T : Any> File.fromJsonFile(): Result<T> = this.readText().fromJson()

/** Encodes this object's JSON string to Base64. */
inline fun <reified T : Any> T.toJson64(): Result<String> =
    runCatching {
        Base64.getEncoder().encodeToString(this.toJson().getOrNull()!!.toByteArray())
    }

/** Decodes a Base64-encoded JSON string into type [T]. */
inline fun <reified T : Any> String.fromJson64(): Result<T> = String(Base64.getDecoder().decode(this)).fromJson()

/** Returns true if this instance's class is annotated with [kotlinx.serialization.Serializable]. */
inline fun <reified T : Any> T.canSerialize(): Boolean = this::class.findAnnotation<Serializable>() != null
