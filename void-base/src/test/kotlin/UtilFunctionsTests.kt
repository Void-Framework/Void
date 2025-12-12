package test

import io.voidx.util.readResourceText
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class UtilFunctionsTests {
    @Test
    fun read_resource_text_via_context_loader() {
        val txt = readResourceText("public/index.html")
        assertTrue(txt.contains("<!DOCTYPE html>"))
    }

    @Test
    fun read_resource_text_via_class_loader() {
        val txt = readResourceText("/public/data.json", UtilFunctionsTests::class.java)
        assertTrue(txt.contains("items"))
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

    @Test
    fun load_deprecated_html_integration_object_via_reflection() {
        // Use reflection to avoid DeprecationLevel.ERROR at compile-time
        val clazz = Class.forName("io.voidx.util.HtmlIntegration")
        assertTrue(clazz.name.endsWith("HtmlIntegration"))
    }
}
