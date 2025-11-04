package io.void.state

import kotlin.reflect.KProperty

class AtomDelegate<T>(initial: T) {
    private val atom = Atom(initial)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = atom.value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        atom.value = value
    }

    fun update(block: (T) -> T) = atom.update(block)
    fun onChange(listener: (T) -> Unit) = atom.onChange(listener)
}

class Atom<T>(initial: T) {
    private val listeners = mutableListOf<(T) -> Unit>()
    private var _value: T = initial

    var value: T
        get() = _value
        set(v) {
            _value = v
            listeners.forEach { it(v) }
        }

    fun update(block: (T) -> T) {
        value = block(value)
    }

    fun onChange(listener: (T) -> Unit) {
        listeners += listener
    }
}
