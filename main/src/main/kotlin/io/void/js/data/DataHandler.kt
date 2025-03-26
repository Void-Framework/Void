package io.void.js.data

import io.void.js.EventDispatcher

class DataHandler private constructor() : IDataHandler {

    companion object {
        val singleton = DataHandler()
    }

    override fun <T> create(value: T): DataHolder<T> {
        return DataHolder(
            value = value,
        )
    }

    override fun <T> update(newData: DataHolder<T>) {
        EventDispatcher.callEvent(DataHolder::class.java, newData)
    }


}