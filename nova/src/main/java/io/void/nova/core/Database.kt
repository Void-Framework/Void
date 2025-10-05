package io.void.nova.core

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

interface Database {

    fun executeQuery(query: PreparedStatement, vararg params: Any): ResultSet
    fun executeUpdate(query: PreparedStatement, vararg params: Any): Int
}
