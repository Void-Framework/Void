package io.voidx.json

import io.voidx.dto.http.RequestDTO
import io.voidx.dto.http.ResponseDTO
import io.voidx.dto.http.buildResponse
import io.voidx.page.Page
import io.voidx.json.JsonConfigs.default
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
 * - Small [io.voidx.dto.http.RequestDTO] helpers like [parseBody] and [detectFormat]
 * - A [autoSerialize] helper to produce a [io.voidx.dto.http.ResponseDTO] from a value based on the request's Accept header
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

/** Parses the textual [io.voidx.dto.http.RequestDTO.body] as JSON into type [T]. */
inline fun <reified T> RequestDTO.parseBody(): Result<T> = runCatching { Json.decodeFromString(this.body) }

/** Detects the request body [Format] based on the `Content-Type` header. */
fun RequestDTO.detectFormat(): Format =
    when (headers["Content-Type"]) {
        "application/json" -> Format.JSON
        "application/cbor" -> Format.CBOR
        "application/xml" -> Format.XML
        else -> Format.TEXT
    }

/**
 * Creates a [io.voidx.dto.http.ResponseDTO] by serializing [value] according to the request `Accept` header.
 * Defaults to `application/json` when the header is missing.
 */
fun Page.autoSerialize(value: Any): ResponseDTO {
    val accept = request.headers["Accept"] ?: "application/json"
    val body =
        when {
            "application/json" in accept -> value.toJson()
            "application/xml" in accept -> value.toXml()
            else -> value.toString()
        }

    return buildResponse {
        headers["Content-Type"] = accept
        this.body = body
    }
}

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
