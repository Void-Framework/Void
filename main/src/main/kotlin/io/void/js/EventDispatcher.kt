package io.void.js

import java.util.concurrent.ConcurrentHashMap

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