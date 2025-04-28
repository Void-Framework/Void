package io.void.api

import io.void.api.method.Method
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.js.JavaScript
import io.void.js.keywords.*
import io.void.js.Function
import io.void.js.FunctionRunner
import io.void.js.FunctionVariable
import io.void.js.Js
import io.void.js.function
import io.void.js.keywords.controlflow.For
import io.void.js.keywords.controlflow.If
import io.void.js.keywords.datastructures.JsList
import io.void.js.keywords.datastructures.JsSet
import io.void.js.keywords.variable.Const
import io.void.js.keywords.variable.const
import io.void.js.keywords.variable.let

internal class VoidJsPage: ApiPage(
    target = "/js/void.js",
    method = Method.GET
) {

    override fun serverGetter(request: RequestDTO): ResponseDTO {
        return if (request.method == method) {
            ResponseDTO(
                status = 200,
                statusText = "All is Well",
                headers = mutableMapOf(
                    "Content-Type" to "application/javascript",
                ),
                body = Js {
                    function<BrowserObject>("elements", listOf("size", "elementName", "text", "attributes")) { args ->
                        val (size, elementName, text, attributes) = args
                        val dom = DOM()
                        dom.fragment()
                        val fragment = const(
                            name = "fragment",
                            value = dom
                        )
                        const(
                            name = "isHTML",
                            value = InlineCall(operation = "/<[^>]+>/.test(text)")
                        )
                        For(
                            condition = "let i = 0; i < size; i++",
                            body = {
                                val element = const(
                                    name = "element",
                                    value = InlineCall(operation = "document.createElement(elementName)")
                                )
                                If(
                                    condition = "isHTML",
                                    body = {
                                        call(
                                            element.asJsValue(),
                                            {
                                                html(text.asJsValue() as VariableValue<String>)
                                            },
                                            HTMLElement()
                                        )
                                    }
                                ).Else {
                                    call(
                                        element.asJsValue(),
                                        {
                                            text(text.asJsValue() as VariableValue<String>)
                                        },
                                        HTMLElement()
                                    )
                                }
                                For("const key in attributes", {
                                    call<Function<Nothing>>(
                                        element.asJsValue(),
                                        "setAttribute(key, attributes[key])"
                                    )
                                })
                                call<Function<Nothing>>(
                                    fragment.asJsValue(),
                                    "appendChild(element);"
                                )
                            }
                        )
                        Return(fragment.name)
                    }
                    function<Nothing>("attributes", listOf("element", "attributes")) { (element, attributes) ->
                        For("const key in attributes") {
                            call(element.asJsValue(), {
                                attribute(FunctionVariable<String>("key").asJsValue(), FunctionVariable<String>("attributes").asJsValue())
                            }, HTMLElement())
                        }
                    }
                    function<Pair<Lambda<*>, Lambda<Nothing>>>("ref", listOf("initialValue")) { (initialValue) ->
                        val deps = const(
                            name = "deps",
                            value = JsSet(emptyJsValue() as JsValue<Lambda<*>>).initialize()
                        )
                        val value = let(
                            name = "value",
                            value = initialValue
                        )
                        val read = const(
                            name = "read",
                            value = Lambda<Any>(_arguments = listOf("collector")) { (collector) ->
                                If("collector") {
                                    call(deps.asJsValue(), {
                                        add(collector.asJsValue() as JsValue<Lambda<*>>)
                                    }, JsSet(emptyJsValue() as JsValue<Lambda<*>>))
                                }
                                Return(value)
                            }
                        )
                        val write = const(
                            name = "write",
                            value = Lambda<Nothing>(_arguments = listOf("newValue")) { (newValue) ->
                                value.Setter(newValue)
                                call(deps.asJsValue(), {
                                    forEach().run(function<Nothing>("run", listOf("fn")) { (fn) ->
                                        InlineCall("fn()")
                                    })
                                }, JsSet(emptyJsValue() as JsValue<Lambda<*>>))
                            }
                        )
                        Return("{ read, write }")
                    }
                    val watchEffect = function<Nothing>("watchEffect", listOf("fn")) { (fn) ->
                        val runner = const(
                            name = "runner",
                            value = Lambda<Nothing>(_arguments = emptyList()) {
                                InlineCall("fn(runner)")
                                InlineCall("runner()")
                            }
                        )
                    }
                    function<Nothing>("bindText", listOf("element", "ref")) { (element, ref) ->
                        watchEffect.run(Lambda<Nothing>(_arguments = listOf("track")) {
                            call(element.asJsValue(), {
                                text(InlineCall("ref.read(track)").asJsValue() as JsValue<String>)
                            }, HTMLElement())
                        }.asJsValue())
                    }
                }.render()
            )
        } else {
            ResponseDTO(
                status = 405,
                statusText = "Method not allowed",
                headers = mutableMapOf(),
                body = ""
            )
        }
    }
}