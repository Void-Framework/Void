package io.void.util

abstract class ModuleInit {
    companion object {
        val initializers = mutableSetOf<ModuleInit>()
    }

    init {
        initializers.add(this)
    }

    abstract fun init()
}