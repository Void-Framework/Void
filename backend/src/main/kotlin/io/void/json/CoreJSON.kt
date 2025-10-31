package io.void.json

import io.void.cache.Cacheable
import io.void.dto.http.RequestDTO
import io.void.dto.http.ResponseDTO
import io.void.dto.http.buildResponse
import io.void.html.page.Page
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
import java.util.Base64
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findAnnotations

object JsonConfigs {
    val default = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val pretty = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}

inline fun <reified T : Any> T.toJson(pretty: Boolean = false): Result<String> = runCatching {
    val json = if (pretty) JsonConfigs.pretty else JsonConfigs.default
    json.encodeToString(this)
}
inline fun <reified T : Any> String.fromJson(): Result<T> = runCatching { Json.decodeFromString(this) }

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.toBytes(): Result<ByteArray> = runCatching { Cbor.encodeToByteArray(this) }
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.fromJson(): Result<T> = runCatching { Cbor.decodeFromByteArray(this) }

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.toXml(): Result<ByteArray> = runCatching { ProtoBuf.encodeToByteArray(this) }
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.fromXml(): Result<T> = runCatching { ProtoBuf.decodeFromByteArray(this) }

inline fun <reified T> RequestDTO.parseBody(): Result<T> = runCatching { Json.decodeFromString(this.body) }

fun RequestDTO.detectFormat(): Format =
    when (headers["Content-Type"]) {
        "application/json" -> Format.JSON
        "application/cbor" -> Format.CBOR
        "application/xml" -> Format.XML
        else -> Format.TEXT
    }

fun Page<*>.autoSerialize(value: Any): ResponseDTO {
    val accept = request.headers["Accept"] ?: "application/json"
    val body = when {
        "application/json" in accept -> value.toJson()
        "application/xml" in accept -> value.toXml()
        else -> value.toString()
    }

    return buildResponse {
        headers["Content-Type"] = accept
        this.body = body
    }
}

inline fun <reified T : Any> T.toJsonFile(pretty: Boolean = false, path: Path) {
    Files.createFile(path)
    Files.write(path, this.toJson(pretty).getOrNull()!!.toByteArray())
}
inline fun <reified T : Any> File.fromJsonFile(): Result<T> = this.readText().fromJson()

inline fun <reified T : Any> T.toJson64(): Result<String> = runCatching { Base64.getEncoder().encodeToString(this.toJson().getOrNull()!!.toByteArray()) }
inline fun <reified T : Any> String.fromJson64(): Result<T> = String(Base64.getDecoder().decode(this)).fromJson()

inline fun <reified T : Any> T.canSerialize(): Boolean = this::class.findAnnotation<Cacheable>() != null

