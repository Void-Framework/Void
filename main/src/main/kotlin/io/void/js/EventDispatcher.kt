package io.void.js

import io.void.js.data.DataHolder
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import kotlin.reflect.full.functions

interface EventDispatcher {

    companion object {
        private val listeners = ConcurrentHashMap<Class<*>, MutableList<(Any) -> Unit>>()

        fun <T : Any> addEventListener(type: Class<T>, listener: (T) -> Unit) {
            listeners.computeIfAbsent(type) { mutableListOf() }
                .add(listener as (Any) -> Unit)
        }

        fun <T : Any> callEvent(type: Class<T>, event: T) {
            listeners[type]?.forEach { listener ->
                listener(event)
            }
        }
    }
}