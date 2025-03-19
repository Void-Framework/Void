package io.void.js.data


interface IDataHandler {

    fun<T> create(value: T): DataHolder<T>
    fun<T> update(newData: DataHolder<T>)
}