package test

import io.voidx.router.Router
import kotlin.test.Test
import kotlin.test.assertEquals

class RouterParseQueryEdgeTests {
    @Test
    fun parse_query_handles_missing_values_and_malformed_encodings() {
        // key without value should be ignored; malformed percent-encoding should be skipped
        val q = Router.Companion.parseQuery("/p?good=ok&noval&bad=%zz&sp=hello+world")
        assertEquals(mapOf("good" to "ok", "sp" to "hello world"), q)
    }

    @Test
    fun parse_query_handles_empty_or_no_query() {
        assertEquals(emptyMap(), Router.Companion.parseQuery("/p"))
        assertEquals(emptyMap(), Router.Companion.parseQuery("/p?"))
    }

    @Test
    fun parse_query_preserves_trailing_slash_in_parameter_value() {
        val q1 = Router.parseQuery("/files?prefix=folder/")
        assertEquals(mapOf("prefix" to "folder/"), q1)

        val q2 = Router.parseQuery("/search?q=path/to/dir/&type=dir/")
        assertEquals(mapOf("q" to "path/to/dir/", "type" to "dir/"), q2)

        val q3 = Router.parseQuery("/api?url=https://example.com/api/v1/")
        assertEquals(mapOf("url" to "https://example.com/api/v1/"), q3)
    }
}
