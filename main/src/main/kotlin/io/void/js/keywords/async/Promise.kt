package io.void.js.keywords.async

import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.JavaScript
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.error.Error

class Promise(): Keyword {

    override var jsReturn: String = ""

    constructor(lambda: PromiseLambda) : this() {
        jsReturn = "${if (await) "await " else ""}new Promise(${lambda.render()})"
    }

    override fun render(): String {
        return jsReturn
    }
    fun then(function: FetchFunction): Promise {
        jsReturn += ".then(${function.render()})"
        return this
    }
    fun catch(function: FetchFunction): Promise {
        jsReturn += ".catch(${function.render()})"
        return this
    }
    fun finally(function: FetchFunction): Promise {
        jsReturn += ".finally(${function.render()})"
        return this
    }
    fun all(list: JsValue<JsList<Promise>>): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.all($list)"
        return Promise()
    }
    fun allSettled(list: JsValue<JsList<Promise>>): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.allSettled($list)"
        return Promise()
    }
    fun any(list: JsValue<JsList<Promise>>): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.any($list)"
        return Promise()
    }
    fun race(list: JsValue<JsList<Promise>>): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.race($list)"
        return Promise()
    }
    fun reject(reason: JsValue<Error>): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.reject($reason)"
        return Promise()
    }
    fun resolve(reason: JsValue<*>): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.reject($reason)"
        return Promise()
    }
    fun jsTry(function: JsValue<Function<*>>, args: JsValue<*> = emptyJsValue()): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.try($function${if (args == emptyJsValue()) "" else ", $args"})"
        return Promise()
    }
    fun withResolvers(): Promise {
        jsReturn = "${if (await) "await " else ""}Promise.withResolvers()"
        return Promise()
    }
}

data class PromiseLambda(
    val resolve: String,
    val reject: String,
    val _body: JavaScript.(List<FunctionVariable<*>>) -> Unit
): Function<Nothing>(
    name = "",
    arguments = listOf(resolve, reject),
    body = _body
) {
    override var jsReturn: String = "($resolve, $reject) => {${children.joinToString(";") { it.render() }}"

    override fun render(): String {
        return jsReturn
    }
}

fun JavaScript.all(list: JsValue<JsList<Promise>>): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.all(list)
}
fun JavaScript.allSettled(list: JsValue<JsList<Promise>>): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.allSettled(list)
}
fun JavaScript.any(list: JsValue<JsList<Promise>>): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.any(list)
}
fun JavaScript.race(list: JsValue<JsList<Promise>>): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.race(list)
}
fun JavaScript.reject(reason: JsValue<Error>): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.reject(reason)
}
fun JavaScript.resolve(reason: JsValue<*>): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.resolve(reason)
}
fun JavaScript.pTry(function: JsValue<Function<*>>, args: JsValue<*> = emptyJsValue()): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.jsTry(function, args)
}
fun JavaScript.withResolvers(): Promise {
    val promise = Promise()
    children.add(promise)
    return promise.withResolvers()
}