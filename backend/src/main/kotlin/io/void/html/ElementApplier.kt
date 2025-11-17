package io.void.html

import androidx.compose.runtime.Applier

class ElementApplier(root: Element) : Applier<Element> {

    // stack of nodes we're currently in
    private val stack = ArrayDeque<Element>().apply { add(root) }

    override val current: Element
        get() = stack.last()

    override fun down(node: Element) {
        // Move "focus" into this node
        stack.addLast(node)
    }

    override fun up() {
        // Move back to parent
        stack.removeLast()
    }

    override fun insertTopDown(index: Int, instance: Element) {
        // Add child into the current node
        current.children?.add(index, instance)
    }

    override fun insertBottomUp(index: Int, instance: Element) {
        // Usually unused for DOM-like trees
        // Compose calls insertTopDown for us
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            current.children?.removeAt(index)
        }
    }

    override fun move(from: Int, to: Int, count: Int) {
        val list = current.children
        val extracted = list?.subList(from, from + count)?.toList()
        repeat(count) { list?.removeAt(from) }
        extracted?.let { list.addAll(to, it) }
    }

    override fun clear() {
        current.children?.clear()
    }
}
