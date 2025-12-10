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
@Deprecated(
    message = "ModuleInit is deprecated and will be removed. Use Bootstrap.Module and onRouterCreated(Context) instead.",
    replaceWith = ReplaceWith(
        expression = "io.voidx.bootstrap.Bootstrap.Module",
        imports = ["io.voidx.bootstrap.Bootstrap"],
    ),
    level = DeprecationLevel.WARNING,
)
abstract class ModuleInit {
    companion object {
        /** Registry of all constructed module instances. */
        internal val initializers = mutableSetOf<ModuleInit>()

        /**
         * Executes all registered legacy module initializers.
         * Exposed for the new bootstrap service to bridge old behavior.
         */
        fun runAllInitializers() {
            initializers.forEach { it.init() }
        }
    }

    init {
        initializers.add(this)
    }

    /** Called during startup to initialize the module. */
    abstract fun init()
}
