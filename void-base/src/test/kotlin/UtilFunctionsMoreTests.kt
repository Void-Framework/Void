package test

import io.voidx.util.toResult
import io.voidx.util.trimTrailingEmpty
import io.voidx.util.toResult as exToResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilFunctionsMoreTests {
    @Test
    fun to_result_success_and_failure() {
        val ok = 42.toResult()
        assertTrue(ok.isSuccess)
        assertEquals(42, ok.getOrNull())

        val ex = IllegalArgumentException("boom")
        val bad: Result<Int> = ex.exToResult()
        assertTrue(bad.isFailure)
        val msg = bad.exceptionOrNull()?.message
        assertEquals("boom", msg)
    }

    @Test
    fun trim_trailing_empty_removes_only_one_empty_tail() {
        val list = mutableListOf("a", "b", "")
        val removed = list.trimTrailingEmpty()
        assertTrue(removed)
        assertEquals(listOf("a", "b"), list)

        val again = list.trimTrailingEmpty()
        assertFalse(again)
        assertEquals(listOf("a", "b"), list)
    }
}
