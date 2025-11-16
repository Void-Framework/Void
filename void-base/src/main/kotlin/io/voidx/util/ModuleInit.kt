package io.voidx.util

import io.voidx.util.ModuleInit.Companion.initializers

/**
 * Base class for framework modules that need to register initialization
 * logic during application startup.
 *
 * Behavior:
 * - Each subclass instance automatically registers itself in [initializers].
 * - At server startup, the framework (or user code) iterates over the registry
 *   and calls [init] on every module.
 *
 * Usage:
 * ```
 * class UserModule : ModuleInit() {
 *     override fun init() {
 *         // setup routes, load config, register services, ...
 *     }
 * }
 *
 * // During startup
 * ModuleInit.initializers.forEach { it.init() }
 * ```
 *
 * Notes:
 * - Registration happens in the constructor via the `init {}` block.
 * - `initializers` is a `MutableSet` to avoid duplicate registrations.
 * - Modules may be instantiated anywhere; they are picked up automatically.
 */
abstract class ModuleInit {
    companion object {
        /** Registry of all constructed module instances. */
        internal val initializers = mutableSetOf<ModuleInit>()
    }

    init {
        initializers.add(this)
    }

    /** Called during startup to initialize the module. */
    abstract fun init()
}
