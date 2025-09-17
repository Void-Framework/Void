package io.jadiefication.routes.home

import io.jadiefication.components.testComponent
import io.void.cache.Cacheable
import io.void.generated.*
import io.void.html.Element
import io.void.html.Fractal
import io.void.html.attributes.AttributeNames
import io.void.html.attributes.attribute
import io.void.html.page.Page
import io.void.html.page.content.ContentType
import io.void.html.page.metadata.Metadata
import io.void.html.page.metadata.MetadataHandler
import java.net.URL
import kotlin.reflect.KClass

@Cacheable(invalidationDurationInMillies = 0)
class HomeRoute : Page<ContentType.HtmlElements>(target = "/") {
    private val containerClasses = "container mx-auto px-4 py-8"
    private val sectionClasses = "bg-white rounded-lg shadow-md p-6 mb-8"
    private val headingClasses = "text-3xl font-bold text-gray-800 mb-4"
    private val subheadingClasses = "text-xl text-gray-600 mb-2"
    private val linkClasses = "text-blue-500 hover:text-blue-700 transition-colors duration-300"
    private val cardClasses = "bg-gray-50 p-4 rounded-lg border border-gray-200"
    private val buttonClasses = "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-all duration-300"
    override var metadata: Metadata? = MetadataHandler.create(page = this, builder = {})
    override val contentType: KClass<ContentType.HtmlElements> = ContentType.HtmlElements::class

    override fun content(): ContentType.HtmlElements =
        ContentType.HtmlElements(
            Main(
                attribute {
                    name = AttributeNames.CLASS
                    value = containerClasses
                },
            ) {
                Header(
                    attribute {
                        name = AttributeNames.CLASS
                        value = "text-center mb-12"
                    },
                ) {
                    H1 {
                        Fractal("Welcome to Void Framework")
                    }
                    Nav(
                        attribute {
                            name = AttributeNames.CLASS
                            value = "flex justify-center gap-4 mt-4"
                        },
                    ) {
                        A(
                            attribute {
                                name = AttributeNames.HREF
                                value = "#features"
                            },
                            attribute {
                                name = AttributeNames.CLASS
                                value = linkClasses
                            },
                        ) { Fractal("Features ") }

                        A(
                            attribute {
                                name = AttributeNames.HREF
                                value = "#docs"
                            },
                            attribute {
                                name = AttributeNames.CLASS
                                value = linkClasses
                            },
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
                    },
                ) {
                    H2(
                        attribute {
                            name = AttributeNames.CLASS
                            value = headingClasses
                        },
                    ) { Fractal("Framework Features") }

                    Article(
                        attribute {
                            name = AttributeNames.CLASS
                            value = cardClasses
                        },
                    ) {
                        H3(
                            attribute {
                                name = AttributeNames.CLASS
                                value = subheadingClasses
                            },
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
                    },
                ) {
                    H2(
                        attribute {
                            name = AttributeNames.CLASS
                            value = headingClasses
                        },
                    ) { Fractal("Documentation") }

                    Div(
                        attribute {
                            name = AttributeNames.CLASS
                            value = "grid grid-cols-1 md:grid-cols-2 gap-4"
                        },
                    ) {
                        Article(
                            attribute {
                                name = AttributeNames.CLASS
                                value = cardClasses
                            },
                        ) {
                            H3(
                                attribute {
                                    name = AttributeNames.CLASS
                                    value = subheadingClasses
                                },
                            ) { Fractal("Getting Started") }

                            P { Fractal("Quick start guide for new projects") }

                            Button(
                                attribute {
                                    name = AttributeNames.CLASS
                                    value = buttonClasses
                                },
                            ) { Fractal("Learn More") }
                        }

                        Article(
                            attribute {
                                name = AttributeNames.CLASS
                                value = cardClasses
                            },
                        ) {
                            H3(
                                attribute {
                                    name = AttributeNames.CLASS
                                    value = subheadingClasses
                                },
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
                                },
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
                    },
                ) {
                    P { Fractal("© 2023 Void Framework. All rights reserved.") }

                    Small {
                        Fractal("Built with ")
                        Code { Fractal("Kotlin") }
                    }
                    testComponent()
                }
            },
        )
}
