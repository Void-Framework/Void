package io.void.nova.core

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.pool.HikariPool
import java.net.URL
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class Nova(
    val url: String,
    val poolSize: Int
) : Database {

    private val pool = HikariDataSource(HikariConfig().apply {
        jdbcUrl = url
        maximumPoolSize = poolSize
    })

    fun <T> useConnection(block: (Connection) -> T): T {
        pool.connection.use { conn ->
            return block(conn)
        }
    }

    override fun executeQuery(query: PreparedStatement, vararg params: Any): ResultSet {
        params.forEachIndexed { index, value ->
            query.setObject(index + 1, value)
        }
        return query.executeQuery()
    }

    override fun executeUpdate(query: PreparedStatement, vararg params: Any): Int {
        params.forEachIndexed { index, value ->
            query.setObject(index + 1, value)
        }
        return query.executeUpdate()
    }
}