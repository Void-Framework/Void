package io.void.js.keywords.async

import io.void.dto.ResponseDTO
import io.void.js.JavaScript
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import java.net.URL

data class FetchFunction(
    val _body: JavaScript.(Function) -> Unit,
    val responseArgName: String
): Function(
    name = "",
    arguments = listOf(responseArgName),
    body = _body
) {
    override var jsReturn: String = "$responseArgName => {${children.joinToString(";") { it.render() }}}"

    override fun render(): String {
        return jsReturn
    }
}

data class Fetch(
    val data: ResponseDTO?,
    val url: URL,
): Keyword {

    private var headers: String = ""

    override var jsReturn: String = "fetch($url${if (data != null) {
        ", {method: '${data.statusText}', headers: {$headers}, body: ${data.body}"
    } else {
        ""
    }
    })"

    init {
        data?.headers?.forEach { (name, value) ->
            headers += "'$name': '$value',"
        }
        if (data?.headers?.isNotEmpty() == true) {
            headers.replaceAfterLast(",", "")
        }
    }

    override fun render(): String {
        return "$jsReturn;"
    }

    fun then(function: FetchFunction): Fetch {
        jsReturn += ".then(${function.render()})"
        return this
    }

    fun catch(function: FetchFunction) {
        jsReturn += ".catch(${function.render()})"
    }
}

fun JavaScript.fetch(data: ResponseDTO?, url: URL): Fetch {
    val fetch = Fetch(
        data= data,
        url = url,
    )
    children.add(fetch)
    return fetch
}