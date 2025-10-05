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

    override fun <T> useConnection(block: (Connection) -> T): T {
        pool.connection.use { conn ->
            return block(conn)
        }
    }

    override fun executeQuery(query: String, vararg params: Any): Result<ResultSet> {
        return useConnection {
            try {
                val result = it.prepareStatement(query).use { statement ->
                    params.forEachIndexed { i, value -> statement.setObject(i + 1, value) }
                    statement.executeQuery().use { result ->
                        result.toResult()
                    }
                }
                return@useConnection result
            } catch (e: Exception) {
                return@useConnection e.toResult()
            }
        }
    }

    override fun executeUpdate(query: String, vararg params: Any): Result<Int> {
        return useConnection {
            try {
                val result = it.prepareStatement(query).use { statement ->
                    params.forEachIndexed { i, value -> statement.setObject(i + 1, value) }
                    statement.executeUpdate().toResult()
                }
                return@useConnection result
            } catch (e: Exception) {
                return@useConnection e.toResult()
            }
        }
    }

    override fun <T> transaction(block: (Connection) -> T): Result<T> {
        pool.connection.use { conn ->
            conn.autoCommit = false
            try {
                val result = block(conn)
                conn.commit()
                return result.toResult()
            } catch (e: Exception) {
                conn.rollback()
                return e.toResult()
            }
        }
    }
}

fun <T> T.toResult(): Result<T> = Result.success(this)

fun <T> Exception.toResult(): Result<T> = Result.failure<T>(this)