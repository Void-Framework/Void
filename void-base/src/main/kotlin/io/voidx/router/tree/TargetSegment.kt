package io.voidx.router.tree

/**
 * Represents a segment of a route target path.
 *
 * @property dynamic True if this segment is a dynamic parameter (e.g., `{id}`).
 * @property optional True if this segment is optional (e.g., `{slug?}`).
 * @property name The name of the segment or parameter.
 */
data class TargetSegment(
    val dynamic: Boolean,
    val optional: Boolean,
    val name: String,
)
