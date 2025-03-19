package io.void.js.data

import io.void.ws.WSClient

class DataHandler private constructor() : IDataHandler {

    companion object {
        val singleton = DataHandler()
    }

    override fun <T> create(value: T, wsClient: WSClient): DataHolder<T> {
        return DataHolder(
            value = value,
            client = wsClient
        )
    }

    override fun <T> send(data: DataHolder<T>) {
        data.client.send(data.render())
    }

    override fun <T> onUpdate(newData: DataHolder<T>) {
        send(
            data = newData
        )
    }

}