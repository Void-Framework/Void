package io.void.js.data

import io.void.ws.WSClient

interface IDataHandler {

    fun<T> create(value: T, wsClient: WSClient): DataHolder<T>
    fun<T> send(data: DataHolder<T>)
    fun<T> onUpdate(newData: DataHolder<T>)
}