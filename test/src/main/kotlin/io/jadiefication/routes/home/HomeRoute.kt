package io.jadiefication.routes.home

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
import io.void.js.keywords.datastructures.forEach
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
        data = setData("Welcome to Void Framework")
        /*val function = function("test", listOf("element")) {
            it.children.add(Call("element", {
                this.HTMLElement().text("HELP")
            }, DOM()))
        }
        id("features").html(Div {
            Fractal("Test")
        })
        forEach(selectAll("class"), function)*/
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