package io.jadiefication.routes.home

import asJsValue
import io.void.generated.*
import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.js.JavaScript
import io.void.js.data.DataHandler
import io.void.js.data.DataHolder
import io.void.js.data.get
import io.void.js.data.setData
import io.void.js.keywords.*
import io.void.js.keywords.Function
import io.void.js.keywords.async.FetchFunction
import io.void.js.keywords.async.fetch
import io.void.js.keywords.controlflow.If
import io.void.js.keywords.controlflow.While
import io.void.js.keywords.datastructures.*
import io.void.js.keywords.event.*
import io.void.js.keywords.variable.const
import io.void.js.keywords.variable.let
import java.net.URL

class HomeRoute : Page(target = "/") {

    private val containerClasses = "container mx-auto px-4 py-8"
    private val sectionClasses = "bg-white rounded-lg shadow-md p-6 mb-8"
    private val headingClasses = "text-3xl font-bold text-gray-800 mb-4"
    private val subheadingClasses = "text-xl text-gray-600 mb-2"
    private val linkClasses = "text-blue-500 hover:text-blue-700 transition-colors duration-300"
    private val cardClasses = "bg-gray-50 p-4 rounded-lg border border-gray-200"
    private val buttonClasses = "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-all duration-300"
    private lateinit var data: DataHolder<String>

    override val javascript: JavaScript = JavaScript(false) {
        // Test data binding
        data = setData("Welcome to Void Framework")

        // Test DOM manipulation and events
        id("features".asJsValue()).html(Div {
            H3 { Fractal("Dynamic Content") }
            P { Fractal("This content was added via JavaScript") }
        }.asJsValue())

        // Test event handling on all buttons
        selectAll("button".asJsValue()).forEach().run(function("handleButtonClick", listOf("button")) {
            it.put(Call("button", {
                this.render()
            }, Event(CustomEvent.getEvent(Events.CLICK), EventFunction(
                stopReload = true,
                _body = { js ->
                    js.put(Call<Function>("console", "log('Button clicked!')"))
                    js.put(Call<DOM>("button", {
                        HTMLElement().text("Clicked!".asJsValue())
                    }, DOM()))
                },
                js = this,
                eventValueName = ""
            ))))
        })

        // Test dynamic class toggling
        selectAll("article".asJsValue()).forEach().run(function("handleArticleHover", listOf("article")) {
            it.put(Call("article", {
                this.render()
            }, Event(CustomEvent.getEvent(Events.MOUSEOVER), EventFunction(
                stopReload = true,
                _body = { js ->
                    js.put(Call<Function>("console", "log('Article hovered!')"))
                    js.put(Call<DOM>("article", {
                        HTMLElement().text("Hover effect active".asJsValue())
                    }, DOM()))
                },
                js = this,
                eventValueName = ""
            ))))
        })

        // Test data updates
        val updateTitleFunction = function("updateTitle", listOf()) {
            it.put(Call<Function>("console", "log('Updating title...')"))
            it.put(data.set("\"Title Updated!\""))
        }

        // Add click event to footer to update title
        id("footer".asJsValue()).html(Button(
            attribute {
                name = AttributeNames.CLASS
                value = buttonClasses
            }
        ) {
            Fractal("Update Title")
        }.asJsValue())

        selectAll("#footer button".asJsValue()).forEach().run(function("footerButton", listOf("button")) {
            it.put(Call("button", {
                this.render()
            }, Event(CustomEvent.getEvent(Events.CLICK), EventFunction(
                stopReload = true,
                _body = { js ->
                    js.put(run(updateTitleFunction, emptyList()))
                },
                js = this,
                eventValueName = ""
            ))))
        })

        // Test data structures
        val userList = const(
            name = "userList",
            value = JsList(listOf("\"John\"", "\"Jane\"", "\"Bob\"")).initialize()
        )
        val userMap = const(
            name = "userMap",
            value = JsMap(mapOf(
                "\"admin\"" to "\"John\"",
                "\"moderator\"" to "\"Jane\"",
                "\"user\"" to "\"Bob\""
            )).initialize()
        )
        val userObject = const(
            name = "userObject",
            value = JsObject(mapOf(
                "name" to "\"John\"",
                "age" to 30,
                "roles" to listOf("\"admin\"", "\"user\"")
            )).initialize()
        )

        // Test control flow with data structures
        call("userList", {
            forEach().run(function("displayUser", listOf("user")) {
                it.put(If("user === 'John'", _body = { body ->
                    body.put(Call<Function>("console", "log('Found admin: ' + user)"))
                }, js = this).ElseIf("user === 'Jane'") { body ->
                    body.put(Call<Function>("console", "log('Found moderator: ' + user)"))
                }.Else { body ->
                    body.put(Call<Function>("console", "log('Found user: ' + user)"))
                })
            })
        }, JsList<String>(listOf()))

        // Test DOM manipulation with data structures
        /*id("user-list").html(Div {
            H3 { Fractal("User List") }
            Ul {
                call("userList", {
                    forEach().run(function("createUserItem", listOf("user")) {
                        it.put(Call("user", {
                            this.HTMLElement().html(Li { Fractal("\${user}") })
                        }, DOM()))
                    })
                }, JsList<String>(listOf()))
            }
        })*/

        // Test fetch with data handling
        fetch(null, URL("https://api.example.com/users"))
            .then(FetchFunction({ js ->
                js.put(Call<Function>("console", "log('Fetched users')"))
                // Process response
                js.put(Call<DOM>("response", {
                    //this.HTMLElement().text("Data fetched successfully")
                }, DOM()))
            }, "response", this))
            .catch(FetchFunction({ js ->
                js.put(Call<Function>("console", "log('Error fetching users')"))
                // Handle error
                js.put(Call<DOM>("error", {
                    //this.HTMLElement().text("Error fetching data")
                }, DOM()))
            }, "error", this))

        // Test loops with map
        let(
            name = "i",
            value = 0
            )
        While("i < 5") {
            it.put(Call<Function>("console", "log('Processing users...')"))
            it.put(Call("userMap", {
                entries().forEach().run(function("processEntry", listOf("entry")) { function ->
                    function.put(Call("entry", {
                        text("Role: \${entry[0]}, User: \${entry[1]}".asJsValue())
                    }, HTMLElement()))
                })
            }, JsMap<String, String>(mapOf())))
            it.put(InlineCall(operation = "i++"))
        }

        // Test object manipulation
        objectMethod("userObject").keys().forEach().run(function("displayKey", listOf("key")) {
            it.put(Call("key", {
                text("User property: \${key}".asJsValue())
            }, HTMLElement()))
        })

        console().log(DOM().elements(5.asJsValue(), Div {
            H3 { Fractal("User List") }
            Ul {
            }}.asJsValue()).asJsValue())

        // Add interactive test button
        id("test-button".asJsValue()).html(Button(
            attribute {
                name = AttributeNames.CLASS
                value = "bg-blue-500 text-white px-4 py-2 rounded"
            }
        ) {
            Fractal("Run Data Structure Tests")
        }.asJsValue())

        // Add event listener for test button
        selectAll("#test-button button".asJsValue()).forEach().run(function("buttonHandler", listOf("button")) {
            it.put(Call("button", {
                this.render()
            }, Event(CustomEvent.getEvent(Events.CLICK), EventFunction(
                stopReload = true,
                _body = { js ->
                    // Test map operations
                    js.put(Call("userMap", {
                        set("newRole", "Alice")
                    }, JsMap<String, String>(mapOf())))
                    js.put(Call<Function>("console", "log('Added new user to map')"))

                    // Test list operations
                    js.put(Call("userMap", {
                        push("Alice")
                    }, JsList<String>(listOf())))
                    js.put(Call<Function>("console", "log('Added new user to list')"))

                    // Test object operations
                    js.put(ObjectsMethods("userObject").delete("age"))
                    js.put(Call<Function>("console", "log('Deleted age from user object')"))
                },
                js = this,
                eventValueName = ""
            ))))
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
