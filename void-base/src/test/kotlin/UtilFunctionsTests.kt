package test

import io.voidx.util.readResourceText
import io.voidx.util.toResult
import io.voidx.util.trimTrailingEmpty
import kotlin.test.*

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

    @Test
    fun toResult_wraps_correctly() {
        val success = "ok".toResult()
        assertTrue(success.isSuccess)
        assertEquals("ok", success.getOrNull())

        val ex = Exception("fail")
        val failure = ex.toResult<String>()
        assertTrue(failure.isFailure)
        assertEquals(ex, failure.exceptionOrNull())
    }

    @Test
    fun trimTrailingEmpty_removes_only_last_if_empty() {
        val list1 = mutableListOf("a", "b", "")
        assertTrue(list1.trimTrailingEmpty())
        assertEquals(listOf("a", "b"), list1)

        val list2 = mutableListOf("a", "b")
        assertFalse(list2.trimTrailingEmpty())
        assertEquals(listOf("a", "b"), list2)

        val list3 = mutableListOf<String>()
        assertFalse(list3.trimTrailingEmpty())

        val list4 = mutableListOf("", "")
        assertTrue(list4.trimTrailingEmpty())
        assertEquals(listOf(""), list4)
    }
}
