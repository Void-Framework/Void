package io.jadiefication.routes.home

import io.void.api.method.Method
import io.void.generated.*
import io.void.html.Fractal
import io.void.html.kts
import io.void.html.page.htmlRoute
import io.void.html.page.invoke
import io.void.html.page.ktsRoute
import java.net.URL

val homeRoute =
    htmlRoute("/", {}) {
        val containerClasses = "container mx-auto px-4 py-8"
        val sectionClasses = "bg-white rounded-lg shadow-md p-6 mb-8"
        val headingClasses = "text-3xl font-bold text-gray-800 mb-4"
        val subheadingClasses = "text-xl text-gray-600 mb-2"
        val linkClasses = "text-blue-500 hover:text-blue-700 transition-colors duration-300"
        val cardClasses = "bg-gray-50 p-4 rounded-lg border border-gray-200"
        val buttonClasses = "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-all duration-300"

        return@htmlRoute Main(
            "class" to containerClasses,
        ) {
            Header(
                "class" to "text-center mb-12",
            ) {
                H1 { Fractal("Welcome to Void Framework") }
                Nav(
                    "class" to "flex justify-center gap-4 mt-4",
                ) {
                    A(
                        "href" to "#features",
                        "class" to linkClasses,
                    ) { Fractal("Features ") }

                    A(
                        "href" to "#docs",
                        "class" to linkClasses,
                    ) { Fractal("Documentation") }
                }
            }

            Section(
                "id" to "features",
                "class" to sectionClasses,
            ) {
                H2("class" to headingClasses) { Fractal("Framework Features") }

                Article("class" to cardClasses) {
                    H3("class" to subheadingClasses) { Fractal("Type-Safe HTML DSL") }
                    P { Fractal("Build your UI with a fully type-safe Kotlin DSL") }
                }
            }

            Section(
                "id" to "docs",
                "class" to sectionClasses,
            ) {
                H2("class" to headingClasses) { Fractal("Documentation") }

                Div("class" to "grid grid-cols-1 md:grid-cols-2 gap-4") {
                    Article("class" to cardClasses) {
                        H3("class" to subheadingClasses) { Fractal("Getting Started") }
                        P { Fractal("Quick start guide for new projects") }
                        Button("class" to buttonClasses) { Fractal("Learn More") }
                    }

                    Article("class" to cardClasses) {
                        H3("class" to subheadingClasses) { Fractal("API Reference") }
                        P { Fractal("Complete API documentation") }

                        A(
                            "href" to URL("https://github.com/Jadiefication/void-framework").toString(),
                            "target" to "_blank",
                            "rel" to "noopener",
                            "class" to buttonClasses,
                        ) { Fractal("View on GitHub") }
                    }
                }
            }

            Footer(
                "id" to "footer",
                "class" to "text-center mt-12 text-gray-600",
            ) {
                P { Fractal("© 2023 Void Framework. All rights reserved.") }

                Small {
                    Fractal("Built with ")
                    Code { Fractal("Kotlin") }
                }

                Section("id" to "kts-test", "class" to sectionClasses) {
                    H2("class" to headingClasses) { Fractal("KTS Test Section") }

                    Button("class" to buttonClasses, "id" to "kts-btn") {
                        Fractal("Say Hello via KTS")

                        // Use your DSL
                        kts {
                            on("/kts-hello", Method.POST)
                            target("#kts-target")
                            swap("innerHTML")
                            trigger("click")
                            confirm("Are you sure you want to send the request?")
                        }
                    }

                    Div("id" to "kts-target", "class" to cardClasses) {
                        Fractal("KTS Response will appear here")
                    }
                }
            }
        }
    }("style.css")

val ktsHelloRoute =
    ktsRoute("/kts-hello") { request, trigger, target ->
        Div("class" to "bg-green-50 p-2 rounded") {
            H3 { Fractal("Hello from KTS!") }
            P { Fractal("Trigger ID: ${trigger?.get("id")}") }
            P { Fractal("Target ID: ${target?.get("id")}") }
        }
    }
