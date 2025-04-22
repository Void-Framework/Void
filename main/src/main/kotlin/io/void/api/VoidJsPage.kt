package io.void.api

import io.void.api.method.Method
import io.void.dto.RequestDTO
import io.void.dto.ResponseDTO
import io.void.js.JavaScript
import io.void.js.keywords.*
import io.void.js.keywords.Function
import io.void.js.keywords.controlflow.For
import io.void.js.keywords.controlflow.If
import io.void.js.keywords.variable.Const
import io.void.js.keywords.variable.const

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
                body = JavaScript {
                    function("elements", listOf("size", "elementName", "text", "attributes")) {
                        val dom = DOM()
                        dom.fragment()
                        val fragment = Const(
                            name = "fragment",
                            value = dom
                        )
                        val isHTML = Const(
                            name = "isHTML",
                            value = InlineCall(operation = "/<[^>]+>/.test(text)")
                        )
                        it.put(
                            fragment
                        )
                        it.put(
                            isHTML
                        )
                        it.put(
                            For(
                                condition = "let i = 0; i < size; i++",
                                _body = { For ->
                                    val element = Const(
                                        name = "element",
                                        value = InlineCall(operation = "document.createElement(elementName)")
                                    )
                                    For.put(element)
                                    For.put(
                                        If(condition = "isHTML",
                                            _body = { If ->
                                                If.put(Call(
                                                    element.asJsValue(),
                                                    {
                                                        html(it.getArg("text").asJsValue())
                                                    },
                                                    HTMLElement()
                                                ))
                                            }, js = this@JavaScript).Else { Else ->
                                                Else.put(Call(
                                                    element.asJsValue(),
                                                    {
                                                        text(it.getArg("text").asJsValue())
                                                    },
                                                    HTMLElement()
                                                ))
                                        }
                                    )
                                    For.put(
                                        For("const key in attributes", { smth ->
                                            smth.put(Call<Function>(
                                                element.asJsValue(),
                                                "setAttribute(key, attributes[key])"
                                            ))
                                        }, this@JavaScript)
                                    )
                                    For.put(
                                        Call<Function>(
                                            fragment.asJsValue(),
                                            "appendChild(element);"
                                        )
                                    )
                                },
                                js = this@JavaScript
                            )
                        )
                        it.put(Return(fragment.name))
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