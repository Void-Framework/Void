package test

import io.voidx.util.readResourceText
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UtilFunctionsTests {
    @Test
    fun read_resource_text_via_context_loader() {
        val txt = readResourceText("public/index.html")
        assertTrue(txt.contains("<!DOCTYPE html>"))
    }

    @Test
    fun read_resource_text_via_class_loader() {
        val txt = readResourceText("/public/data.json", UtilFunctionsTests::class.java)
        assertTrue(txt.contains("a"))
    }

    @Test
    fun read_resource_missing_throws_context_loader() {
        assertFailsWith<IllegalStateException> {
            readResourceText("nope/never.txt")
        }
    }

    @Test
    fun read_resource_missing_throws_class_loader() {
        assertFailsWith<IllegalStateException> {
            readResourceText("/nope/never.txt", UtilFunctionsTests::class.java)
        }
    }
}
