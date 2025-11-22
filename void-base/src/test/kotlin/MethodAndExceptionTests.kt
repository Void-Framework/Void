package test

import io.voidx.Method
import io.voidx.exception.NotEnoughCarriers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MethodAndExceptionTests {
    @Test
    fun method_enum_contains_all_expected_values() {
        val names = Method.entries.map { it.name }.toSet()
        // Standard methods
        assertTrue("GET" in names)
        assertTrue("HEAD" in names)
        assertTrue("POST" in names)
        assertTrue("PUT" in names)
        assertTrue("DELETE" in names)
        assertTrue("CONNECT" in names)
        assertTrue("OPTIONS" in names)
        assertTrue("TRACE" in names)
        assertTrue("PATCH" in names)
        // Fun/custom ones
        assertTrue("BREW" in names)
        assertTrue("PROPFIND" in names)
        assertTrue("WHEN" in names)
    }

    @Test
    fun not_enough_carriers_exception_message_is_descriptive() {
        val ex = NotEnoughCarriers()
        assertEquals(
            "There aren't enough carriers available to support transmission of the packets.",
            ex.message,
        )
    }
}
