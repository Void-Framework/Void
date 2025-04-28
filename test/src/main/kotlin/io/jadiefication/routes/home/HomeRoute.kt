package io.jadiefication.routes.home

import io.void.generated.*
import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.js.JavaScript
import io.void.js.Js
import io.void.js.data.DataHandler
import io.void.js.data.DataHolder
import io.void.js.data.get
import io.void.js.data.setData
import io.void.js.function
import io.void.js.keywords.*
import io.void.js.keywords.async.FetchFunction
import io.void.js.keywords.async.fetch
import io.void.js.keywords.controlflow.If
import io.void.js.keywords.datastructures.*
import io.void.js.keywords.event.*
import io.void.js.keywords.variable.Const
import io.void.js.keywords.variable.const
import io.void.js.keywords.variable.let
import io.void.js.run
import java.net.URL

class HomeRoute : Page(target = "/") {

    private val containerClasses = "container mx-auto px-4 py-8"
    private val sectionClasses = "bg-white rounded-lg shadow-md p-6 mb-8"
    private val headingClasses = "text-3xl font-bold text-gray-800 mb-4"
    private val subheadingClasses = "text-xl text-gray-600 mb-2"
    private val linkClasses = "text-blue-500 hover:text-blue-700 transition-colors duration-300"
    private val cardClasses = "bg-gray-50 p-4 rounded-lg border border-gray-200"
    private val buttonClasses = "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-all duration-300"
    private lateinit var data: Const<DataHolder>

    override val javascript: JavaScript = Js(false) {
        // Test data binding
        data = setData("Welcome to Void Framework".asJsValue(), "data")

        // Test DOM manipulation and events
        id("features".asJsValue()).html(Div {
            H3 { Fractal("Dynamic Content") }
            P { Fractal("This content was added via JavaScript") }
        }.asJsValue())

        // Test event handling on all buttons
        selectAll("button".asJsValue()).forEach().run(function<Nothing>("handleButtonClick", listOf("button")) { (button) ->
            call(
                button.asJsValue(), {}, Event(
                    CustomEvent.getEvent(Events.CLICK).asJsValue(), EventFunction(
                        stopReload = true,
                        _body = { js ->
                            console().log("Button Clicked!".asJsValue())
                            call<DOM>("button".asJsValue(), {
                                HTMLElement().text("Clicked!".asJsValue())
                        }, DOM())
                    },
                        eventValueName = ""
                    ).asJsValue()
                )
            )
        })

        // Test dynamic class toggling
        selectAll("article".asJsValue()).forEach().run(function<Nothing>("handleArticleHover", listOf("article")) { (article) ->
            call(
                article.asJsValue(), {}, Event(
                    CustomEvent.getEvent(Events.MOUSEOVER).asJsValue(), EventFunction(
                        stopReload = true,
                        _body = { js ->
                            console().log("Article Hovered!".asJsValue())
                            call<DOM>("article".asJsValue(), {
                                HTMLElement().text("Hover effect active".asJsValue())
                            }, DOM())
                        },
                        eventValueName = ""
                    ).asJsValue()
                )
            )
        })

        // Test data updates
        val updateTitleFunction = function<Nothing>("updateTitle", listOf()) {
            console().log("Updating tittle...".asJsValue())
            call(data.asJsValue(), {
                write("Title Updated!".asJsValue())
            }, DataHolder(this))
        }

        // Add click event to footer to update title
        id("footer".asJsValue()).html(
            Button(
            attribute {
                name = AttributeNames.CLASS
                value = buttonClasses
            }
        ) {
            Fractal("Update Title")
        }.asJsValue()
        )

        selectAll("#footer".asJsValue()).forEach().run(function<Nothing>("footerButton", listOf("button")) { (button) ->
            call(button.asJsValue(), {}, Event(
                CustomEvent.getEvent(Events.CLICK).asJsValue(), EventFunction(
                    stopReload = true,
                    _body = {
                        run(updateTitleFunction, emptyJsValue())
                    },
                    eventValueName = ""
                ).asJsValue())
            )
        })

        // Test data structures
        val userList = const(
            name = "userList",
            value = JsList(listOf("John", "Jane", "Bob").asJsValue()).initialize()
        )
        val userMap = const(
            name = "userMap",
            value = JsMap(
                mapOf(
                    "\"admin\"" to "\"John\"",
                    "\"moderator\"" to "\"Jane\"",
                    "\"user\"" to "\"Bob\""
                )
            ).initialize()
        )
        val userObject = const(
            name = "userObject",
            value = JsObject(
                mapOf(
                    "name" to "John".asJsValue(),
                    "age" to 30.asJsValue(),
                    "roles" to JsList(listOf("admin", "user").asJsValue()).initialize().asJsValue()
                )
            ).initialize()
        )

        // Test control flow with data structures
        call(userList.asJsValue(), {
            forEach().run(function<Nothing>("displayUser", listOf("user")) { (user) ->
                If("user === 'John'", body = {
                    console().log("Found admin \${user}".asJsValue())
                }).ElseIf("user === 'Jane'") {
                    console().log("Found moderator \${user}".asJsValue())
                }.Else {
                    console().log("Found user \${user}".asJsValue())
                }
            })
        }, JsList(emptyJsValue() as JsValue<String>))

        // Test fetch with data handling
        fetch(null, URL("https://api.example.com/users"))
            .then(FetchFunction({ (result) ->
                console().log("Fetched users".asJsValue())
            }, "response"))
            .catch(FetchFunction({ (error) ->
                console().log("Error fetching users".asJsValue())
                // Handle error
            }, "error"))

        // Test loops with map
        let(
            name = "i",
            value = 0
        )

        // Test object manipulation
        objectMethod(userObject.asJsValue()).keys().forEach().run(function<Nothing>("displayKey", listOf("key")) { (key) ->
            call(key.asJsValue(), {
                text("User property: \${key}".asJsValue())
            }, HTMLElement())
        })

        console().log(DOM().elements(5.asJsValue(), Div {
            H3 { Fractal("User List") }
            Ul {
            }
        }.asJsValue()).asJsValue())

        // Add interactive test button
        id("test-button".asJsValue()).html(
            Button(
            attribute {
                name = AttributeNames.CLASS
                value = "bg-blue-500 text-white px-4 py-2 rounded"
            }
        ) {
            Fractal("Run Data Structure Tests")
        }.asJsValue()
        )

        // Add event listener for test button
        selectAll("#test-button button".asJsValue()).forEach()
            .run(function<Nothing>("buttonHandler", listOf("button")) { (button) ->
                call(button.asJsValue(), {}, Event(
                    CustomEvent.getEvent(Events.CLICK).asJsValue(), EventFunction(
                        stopReload = true,
                        _body = { js ->
                            // Test map operations
                            call("userMap".asJsValue(), {
                                set("newRole".asJsValue(), "Alice".asJsValue())
                            }, JsMap<String, String>(mapOf()))
                            console().log("Added new user to map".asJsValue())

                            // Test list operations
                            call(userMap.asJsValue(), {
                                push("Alice".asJsValue())
                            }, JsList(emptyJsValue() as JsValue<String>))
                            console().log("Added new user to list".asJsValue())

                            // Test object operations
                            objectMethod(userObject.asJsValue()).delete("age")
                            console().log("Deleted age from user object".asJsValue())
                        },
                        eventValueName = ""
                    ).asJsValue()
                )
                )
            })
    }

    override var content: Element? = Main(
        attribute {
            name = AttributeNames.CLASS
            value = containerClasses
        }
    ) {
        Header(
            attribute {
                name = AttributeNames.CLASS
                value = "text-center mb-12"
            }
        ) {
            H1 {
                this.get(data)
            }
            Nav(
                attribute {
                    name = AttributeNames.CLASS
                    value = "flex justify-center gap-4 mt-4"
                }
            ) {
                A(
                    attribute {
                        name = AttributeNames.HREF
                        value = "#features"
                    },
                    attribute {
                        name = AttributeNames.CLASS
                        value = linkClasses
                    }
                ) { Fractal("Features ") }

                A(
                    attribute {
                        name = AttributeNames.HREF
                        value = "#docs"
                    },
                    attribute {
                        name = AttributeNames.CLASS
                        value = linkClasses
                    }
                ) { Fractal("Documentation") }
            }
        }

        Section(
            attribute {
                name = AttributeNames.ID
                value = "features"
            },
            attribute {
                name = AttributeNames.CLASS
                value = sectionClasses
            }
        ) {
            H2(
                attribute {
                    name = AttributeNames.CLASS
                    value = headingClasses
                }
            ) { Fractal("Framework Features") }

            Article(
                attribute {
                    name = AttributeNames.CLASS
                    value = cardClasses
                }
            ) {
                H3(
                    attribute {
                        name = AttributeNames.CLASS
                        value = subheadingClasses
                    }
                ) { Fractal("Type-Safe HTML DSL") }

                P { Fractal("Build your UI with a fully type-safe Kotlin DSL") }
            }
        }

        Section(
            attribute {
                name = AttributeNames.ID
                value = "docs"
            },
            attribute {
                name = AttributeNames.CLASS
                value = sectionClasses
            }
        ) {
            H2(
                attribute {
                    name = AttributeNames.CLASS
                    value = headingClasses
                }
            ) { Fractal("Documentation") }

            Div(
                attribute {
                    name = AttributeNames.CLASS
                    value = "grid grid-cols-1 md:grid-cols-2 gap-4"
                }
            ) {
                Article(
                    attribute {
                        name = AttributeNames.CLASS
                        value = cardClasses
                    }
                ) {
                    H3(
                        attribute {
                            name = AttributeNames.CLASS
                            value = subheadingClasses
                        }
                    ) { Fractal("Getting Started") }

                    P { Fractal("Quick start guide for new projects") }

                    Button(
                        attribute {
                            name = AttributeNames.CLASS
                            value = buttonClasses
                        }
                    ) { Fractal("Learn More") }
                }

                Article(
                    attribute {
                        name = AttributeNames.CLASS
                        value = cardClasses
                    }
                ) {
                    H3(
                        attribute {
                            name = AttributeNames.CLASS
                            value = subheadingClasses
                        }
                    ) { Fractal("API Reference") }

                    P { Fractal("Complete API documentation") }

                    A(
                        attribute {
                            name = AttributeNames.HREF
                            value = URL("https://github.com/Jadiefication/void-framework")
                        },
                        attribute {
                            name = AttributeNames.TARGET
                            value = "_blank"
                        },
                        attribute {
                            name = AttributeNames.REL
                            value = "noopener"
                        },
                        attribute {
                            name = AttributeNames.CLASS
                            value = buttonClasses
                        }
                    ) { Fractal("View on GitHub") }
                }
            }
        }

        Footer(
            attribute {
                name = AttributeNames.ID
                value = "footer"
            },
            attribute {
                name = AttributeNames.CLASS
                value = "text-center mt-12 text-gray-600"
            }
        ) {
            P { Fractal("© 2023 Void Framework. All rights reserved.") }

            Small {
                Fractal("Built with ")
                Code { Fractal("Kotlin") }
            }
        }
    }
}
