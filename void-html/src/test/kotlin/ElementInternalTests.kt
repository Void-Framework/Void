package test

import io.voidx.generated.Div
import io.voidx.generated.H2
import io.voidx.html.Fractal
import io.voidx.html.loop
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ElementInternalTests {
    @Test
    fun addAttributes_and_get_operator_work() {
        val el = Div { }
        el.addAttributes("id" to "root", "class" to "box")
        assertEquals("root", el["id"])
        assertEquals("box", el["class"])
    }

    @Test
    fun unary_plus_adds_text_child_and_render_contains_text() {
        val el =
            Div("id" to "r") {
                +"Hello"
            }
        val s = el.render()
        assertTrue(s.contains("Hello"))
        assertTrue(s.contains("<div"))
    }

    @Test
    fun loop_helper_repeats_children_in_fragment() {
        val parent = Div { }
        val fragment =
            parent.loop(1..3) { idx ->
                H2 { Fractal("Item $idx") }
            }
        // Attach produced fragment to parent
        parent.children!!.add(fragment)
        val rendered = parent.render()
        // Expect three occurrences
        assertTrue(rendered.contains("Item 1"))
        assertTrue(rendered.contains("Item 2"))
        assertTrue(rendered.contains("Item 3"))
    }

    @Test
    fun findElement_returns_first_match_depth_first() {
        val root =
            Div("id" to "root") {
                Div("class" to "box") {
                    H2("id" to "title") { }
                }
                Div("class" to "box") { }
            }
        val found = root.findElement(".box")
        assertNotNull(found)
        assertEquals("div", found.name)
    }
}
