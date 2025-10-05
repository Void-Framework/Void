package io.void.nova.core

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

interface Database {
    fun <T> useConnection(block: (Connection) -> T): T

    fun executeQuery(query: String, vararg params: Any): Result<ResultSet>
    fun executeUpdate(query: String, vararg params: Any): Result<Int>

    fun <T> transaction(block: (Connection) -> T): Result<T>
}
