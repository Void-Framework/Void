package io.void.js.data

import io.void.js.JavaScript
import io.void.js.keywords.Function


interface IDataHandler {

    fun<T> create(value: T, js: JavaScript): Pair<DataHolder<T>, Function>
    fun<T> update(newData: DataHolder<T>)
}