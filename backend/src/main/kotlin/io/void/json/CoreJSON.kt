package io.void.json

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

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
inline fun <reified T : Any> String.fromJson(pretty: Boolean = false): Result<T> = runCatching {
    val json = if (pretty) JsonConfigs.pretty else JsonConfigs.default
    json.decodeFromString(this)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.toBytes(): Result<ByteArray> = runCatching { Cbor.encodeToByteArray(this) }
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.fromJson(): Result<T> = runCatching { Cbor.decodeFromByteArray(this) }