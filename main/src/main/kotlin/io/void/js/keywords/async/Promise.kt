package io.void.js.keywords.async

import io.void.js.Function
import io.void.js.FunctionVariable
import io.void.js.JavaScript
import io.void.js.keywords.Call
import io.void.js.keywords.JsValue
import io.void.js.keywords.Keyword
import io.void.js.keywords.Reference
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.emptyJsValue
import io.void.js.keywords.error.Error
import io.void.js.keywords.refer

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
    fun finally(function: FetchFunction): Reference<Promise> {
        jsReturn += ".finally(${function.render()})"
        return this.refer()
    }
    fun all(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.all($list)"
        return applyMethods(call, Promise(), this)
    }
    fun allSettled(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.allSettled($list)"
        return applyMethods(call, Promise(), this)
    }
    fun any(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.any($list)"
        return applyMethods(call, Promise(), this)
    }
    fun race(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.race($list)"
        return applyMethods(call, Promise(), this)
    }
    fun reject(reason: JsValue<Error>, call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.reject($reason)"
        return applyMethods(call, Promise(), this)
    }
    fun resolve(reason: JsValue<*>, call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.reject($reason)"
        return applyMethods(call, Promise(), this)
    }
    fun jsTry(function: JsValue<Function<*>>, args: JsValue<*> = emptyJsValue(), call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.try($function${if (args == emptyJsValue()) "" else ", $args"})"
        return applyMethods(call, Promise(), this)
    }
    fun withResolvers(call: (Promise) -> Unit): Reference<Promise> {
        jsReturn = "${if (await) "await " else ""}Promise.withResolvers()"
        return applyMethods(call, Promise(), this)
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

fun JavaScript.all(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.all(list, call)
    return promise.refer()
}
fun JavaScript.allSettled(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.allSettled(list, call)
    return promise.refer()
}
fun JavaScript.any(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.any(list, call)
    return promise.refer()
}
fun JavaScript.race(list: JsValue<JsList<Promise>>, call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.race(list, call)
    return promise.refer()
}
fun JavaScript.reject(reason: JsValue<Error>, call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.reject(reason, call)
    return promise.refer()
}
fun JavaScript.resolve(reason: JsValue<*>, call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.resolve(reason, call)
    return promise.refer()
}
fun JavaScript.pTry(function: JsValue<Function<*>>, args: JsValue<*> = emptyJsValue(), call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.jsTry(function, args, call)
    return promise.refer()
}
fun JavaScript.withResolvers(call: (Promise) -> Unit): Reference<Promise> {
    val promise = Promise()
    children.add(promise)
    promise.withResolvers(call)
    return promise.refer()
}