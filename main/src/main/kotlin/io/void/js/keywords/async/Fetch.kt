package io.void.js.keywords.async

import io.void.dto.ResponseDTO
import io.void.js.JavaScript
import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.asJsValue
import io.void.js.keywords.refer
import java.net.URL

data class FetchFunction(
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit,
    val responseArgName: String
): Function<Nothing>(
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
    val url: JsValue<String>,
    val await: Boolean = false
): Keyword {

    private var headers: String = ""

    constructor(data: ResponseDTO?, url: URL, await: Boolean = false): this(
        data = data,
        url = url.toString().asJsValue(),
        await = await
    )

    override var jsReturn: String = "${if (await) "await " else ""}fetch($url${if (data != null) {
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

    fun then(function: FetchFunction): Fetch {
        jsReturn += ".then(${function.render()})"
        return this
    }
    fun catch(function: FetchFunction): Fetch {
        jsReturn += ".catch(${function.render()})"
        return this
    }
    fun finally(function: FetchFunction): Reference<Fetch> {
        jsReturn += ".finally(${function.render()})"
        return this.refer()
    }

    override fun render(): String {
        return "$jsReturn;"
    }
}

fun JavaScript.fetch(data: ResponseDTO?, url: URL, await: Boolean = false): Fetch {
    val fetch = Fetch(
        data= data,
        url = url,
        await = await
    )
    children.add(fetch)
    return fetch
}