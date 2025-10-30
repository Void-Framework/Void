package io.void.nova.table

data class Column<T : Any>(
    var sqlType: String,
    var name: String,
    val primaryKey: Boolean = false,
    val nullable: Boolean = false,
    val defaultValue: T? = null
)