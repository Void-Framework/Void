package io.jadiefication.routes.home

import io.void.generated.*
import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.js.Function
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
import io.void.js.keywords.variable.set
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
        // Data binding with reactive updates
        data = setData("Welcome to Void Framework".asJsValue(), "data")

        // DOM manipulation - add dynamic content
        id("features".asJsValue()).html(Div {
            H3 { Fractal("Dynamic Content") }
            P { Fractal("This content was dynamically inserted via JavaScript") }
            Ul(attribute {
                name = AttributeNames.ID
                value = "dynamic-list"
            }) {
                // Will be populated later
            }
        }.asJsValue())

        // Create a counter for demonstrating state
        var counter = let(
            name = "counter",
            value = 0
        )

        // Create a function to update counter display
        val updateCounterDisplay = function<Nothing>("updateCounterDisplay", emptyList()) {
            id("counter-value".asJsValue()).text(counter.asJsValue() as JsValue<String>)
        }

        // Add counter UI
        id("counter-container".asJsValue()).html(Div {
            H3 { Fractal("State Management Demo") }
            P {
                Fractal("Counter value: ")
                Span(attribute {
                    name = AttributeNames.ID
                    value = "counter-value"
                }) { Fractal("0") }
            }
            Button(
                attribute {
                    name = AttributeNames.ID
                    value = "increment-button"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = buttonClasses
                }
            ) { Fractal("Increment") }
            Button(
                attribute {
                    name = AttributeNames.ID
                    value = "decrement-button"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = buttonClasses
                }
            ) { Fractal("Decrement") }
        }.asJsValue())

        // Add event listeners for counter buttons
        id("increment-button".asJsValue()).on(Events.CLICK.asJsValue(), {
            counter++
            run(updateCounterDisplay, emptyJsValue())
        })
        id("decrement-button".asJsValue()).on(Events.CLICK.asJsValue(), {
            counter--
            run(updateCounterDisplay, emptyJsValue())
        })

        // Test data structures - create a user list
        val users = const(
            name = "users",
            value = JsList(listOf(
                JsObject(mapOf(
                    "id" to 1.asJsValue(),
                    "name" to "John Doe".asJsValue(),
                    "role" to "Admin".asJsValue()
                )).initialize(),
                JsObject(mapOf(
                    "id" to 2.asJsValue(),
                    "name" to "Jane Smith".asJsValue(),
                    "role" to "Developer".asJsValue()
                )).initialize(),
                JsObject(mapOf(
                    "id" to 3.asJsValue(),
                    "name" to "Bob Johnson".asJsValue(),
                    "role" to "Designer".asJsValue()
                )).initialize()
            ).asJsValue()).initialize()
        )

        // Function to render user list
        val renderUserList = function<Nothing>("renderUserList", emptyList()) {
            val listHtml = let(
                name = "listHtml",
                value = ""
            )

            call(users.asJsValue(), {
                forEach { (user) ->
                    // Build HTML for each user
                    set(listHtml, "\${${listHtml.name}} <li class='p-2 border-b'>\${${user.name}.name} - \${${user.name}.role}</li>")
                }
            }, JsList(emptyJsValue() as JsValue<JsObject>))

            // Update the DOM with our list
            id("dynamic-list".asJsValue()).html(listHtml.asJsValue())
        }

        // Initial render
        run(renderUserList, emptyJsValue())

        // Add a form to add new users
        id("user-form-container".asJsValue()).html(Div {
            H3 { Fractal("Add New User") }
            Form(
                attribute {
                    name = AttributeNames.ID
                    value = "user-form"
                }
            ) {
                Div {
                    Label(
                        attribute {
                            name = AttributeNames.FOR
                            value = "user-name"
                        }
                    ) { Fractal("Name:") }
                    Input(
                        attribute {
                            name = AttributeNames.ID
                            value = "user-name"
                        },
                        attribute {
                            name = AttributeNames.TYPE
                            value = "text"
                        },
                        attribute {
                            name = AttributeNames.CLASS
                            value = "border p-2 w-full"
                        }
                    )
                }
                Div {
                    Label(
                        attribute {
                            name = AttributeNames.FOR
                            value = "user-role"
                        }
                    ) { Fractal("Role:") }
                    Input(
                        attribute {
                            name = AttributeNames.ID
                            value = "user-role"
                        },
                        attribute {
                            name = AttributeNames.TYPE
                            value = "text"
                        },
                        attribute {
                            name = AttributeNames.CLASS
                            value = "border p-2 w-full"
                        }
                    )
                }
                Button(
                    attribute {
                        name = AttributeNames.ID
                        value = "add-user-button"
                    },
                    attribute {
                        name = AttributeNames.TYPE
                        value = "submit"
                    },
                    attribute {
                        name = AttributeNames.CLASS
                        value = buttonClasses
                    }
                ) { Fractal("Add User") }
            }
        }.asJsValue())

        // Handle form submission
        id("user-form".asJsValue()).on(Events.SUBMIT.asJsValue(), { (event) ->
            // Prevent default form submission
            val userName = const(
                name = "userName",
                value = Call<Function<Nothing>>(DOM().id("user-name".asJsValue()).asJsValue(), ".value")
            )
            val userRole = const(
                name = "userRole",
                value = Call<Function<Nothing>>(DOM().id("user-role".asJsValue()).asJsValue(), ".value")
            )

            If("!userName || !userRole") {
                alert("Please fill in all fields".asJsValue())
                Return()
            }

            val newUser = const(
                name = "newUser",
                value = JsObject(mapOf(
                    "id" to "Date.now()".asJsValue(),
                    "name" to userName.asJsValue(),
                    "role" to userRole.asJsValue()
                )).initialize()
            )
            call(users.asJsValue(), {
                push(newUser.asJsValue() as JsValue<JsObject>)
            }, JsList<JsObject>(JsObject(emptyMap()).asJsValue()))

            call<Function<Nothing>>(DOM().id("user-name".asJsValue()).asJsValue(), ".value = \"\"")
            call<Function<Nothing>>(DOM().id("user-role".asJsValue()).asJsValue(), ".value = \"\"")

            run(renderUserList, emptyJsValue())
            alert("User added successfully!".asJsValue())
        })

        // Test fetch API with async/await
        val fetchData = function<Nothing>("fetchData", emptyList()) {
            id("fetch-status".asJsValue()).text("Loading...".asJsValue())

            fetch(null, URL("https://jsonplaceholder.typicode.com/todos/1"))
                .then(FetchFunction({ (response) ->
                    table(response.asJsValue())
                }))
                .then(FetchFunction({ (data) ->
                    id("fetch-status".asJsValue()).text("Data loaded!".asJsValue())
                    id("fetch-result".asJsValue()).html(
                        Pre {
                            Code {
                                Fractal("JSON.stringify(${data.name}, null, 2)")
                            }
                        }.asJsValue()
                    )
                }))
                .catch(FetchFunction({ (error) ->
                    id("fetch-status".asJsValue()).text("Error loading data".asJsValue())
                    id("fetch-result".asJsValue()).text(error.asJsValue() as JsValue<String>)
                }))
        }

        // Add fetch test UI
        id("fetch-container".asJsValue()).html(Div {
            H3 { Fractal("Fetch API Test") }
            Button(
                attribute {
                    name = AttributeNames.ID
                    value = "fetch-button"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = buttonClasses
                }
            ) { Fractal("Fetch Data") }
            Div(
                attribute {
                    name = AttributeNames.ID
                    value = "fetch-status"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = "mt-2"
                }
            ) { Fractal("Click button to fetch data") }
            Div(
                attribute {
                    name = AttributeNames.ID
                    value = "fetch-result"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = "mt-2 p-4 bg-gray-100 rounded"
                }
            ) {}
        }.asJsValue())

        // Add event listener for fetch button
        id("fetch-button".asJsValue()).on(Events.CLICK.asJsValue()) {
            run(fetchData, emptyJsValue())
        }

        // Test class toggling
        /*val toggleClass = function<Nothing>("toggleClass", listOf("element", "className")) { (element, className) ->
            call(element.asJsValue(), {
                classList().toggle(className.asJsValue())
            }, HTMLElement())
        }*/

        // Add class toggle test
        id("toggle-container".asJsValue()).html(Div {
            H3 { Fractal("Class Toggle Test") }
            Div(
                attribute {
                    name = AttributeNames.ID
                    value = "toggle-element"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = "p-4 bg-gray-200 transition-all duration-300"
                }
            ) { Fractal("Click me to toggle class") }
        }.asJsValue())

        // Add event listener for toggle element
        /*id("toggle-element".asJsValue()).on(Events.CLICK.asJsValue()) { (event) ->
            call(event.asJsValue(), {
                target()
            }, EventFunction(
                stopReload = false,
                _body = { js ->
                    run(toggleClass, listOf("event.target".asJsValue(), "bg-blue-300".asJsValue()))
                },
                eventValueName = "event"
            ))
        }*/

        // Update title function
        val updateTitle = function<Nothing>("updateTitle", emptyList()) {
            log("Updating title...".asJsValue())
            call(data.asJsValue(), {
                write("Void Framework - Interactive Demo".asJsValue())
            }, DataHolder(this))
        }

        // Add update title button
        id("footer".asJsValue()).html(
            Button(
                attribute {
                    name = AttributeNames.ID
                    value = "update-title-button"
                },
                attribute {
                    name = AttributeNames.CLASS
                    value = buttonClasses
                }
            ) { Fractal("Update Page Title") }.asJsValue()
        ).asJsValue()

        // Add event listener for update title button
        id("update-title-button".asJsValue()).on(Events.CLICK.asJsValue()) {
            run(updateTitle, emptyJsValue())
        }
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
