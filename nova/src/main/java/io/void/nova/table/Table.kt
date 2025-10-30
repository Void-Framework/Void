package io.void.nova.table

import java.time.LocalDateTime

abstract class Table<T> {

    abstract val id: Int
    internal val columns = mutableListOf<Column<*>>()

    fun int(
        name: String? = null,
        primaryKey: Boolean = false,
        nullable: Boolean = false,
        default: Int? = null
    ): Column<Int> = register(Column(name ?: "int_col", "INTEGER", primaryKey, nullable, default))

    fun long(
        name: String? = null,
        primaryKey: Boolean = false,
        nullable: Boolean = false,
        default: Long? = null
    ): Column<Long> = register(Column(name ?: "long_col", "BIGINT", primaryKey, nullable, default))

    fun varchar(
        length: Int,
        name: String? = null,
        nullable: Boolean = false,
        default: String? = null
    ): Column<String> = register(Column(name ?: "varchar_col", "VARCHAR($length)", false, nullable, default))

    fun text(
        name: String? = null,
        nullable: Boolean = false,
        default: String? = null
    ): Column<String> = register(Column(name ?: "text_col", "TEXT", false, nullable, default))

    fun bool(
        name: String? = null,
        nullable: Boolean = false,
        default: Boolean? = null
    ): Column<Boolean> = register(Column(name ?: "bool_col", "BOOLEAN", false, nullable, default))

    fun double(
        name: String? = null,
        nullable: Boolean = false,
        default: Double? = null
    ): Column<Double> = register(Column(name ?: "double_col", "DOUBLE", false, nullable, default))

    fun datetime(
        name: String? = null,
        nullable: Boolean = false,
        default: LocalDateTime? = null
    ): Column<LocalDateTime> = register(Column(name ?: "datetime_col", "TIMESTAMP", false, nullable, default))

    private fun <T : Any> register(column: Column<T>): Column<T> {
        columns += column
        return column
    }
}