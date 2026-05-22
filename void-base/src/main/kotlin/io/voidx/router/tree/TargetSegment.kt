package io.voidx.router.tree

data class TargetSegment(
    val dynamic: Boolean,
    val optional: Boolean,
    val name: String
) {
}