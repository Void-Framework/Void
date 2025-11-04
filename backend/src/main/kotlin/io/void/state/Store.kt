package io.void.state

class Store {
    companion object {
        fun <T> atom(initial: T) = AtomDelegate(initial)
    }
}
