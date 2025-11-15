package io.void.docs

import io.void.api.method.Method
import io.void.dto.http.buildResponse
import io.void.dto.http.headers
import io.void.generated.*
import io.void.html.Fractal
import io.void.html.kts
import io.void.html.page.htmlRoute
import io.void.html.page.invoke
import io.void.html.page.ktsRoute
import io.void.server.simpleServer

/**
 * A simple Docs homepage built using the same DSL style as the test module.
 */
val docsHomeRoute =
    htmlRoute("/", {}) {
        val container = "container mx-auto px-4 py-8"
        val section = "bg-white rounded-lg shadow-md p-6 mb-8"
        val heading = "text-3xl font-bold text-gray-800 mb-4"
        val subheading = "text-xl text-gray-600 mb-2"
        val link = "text-blue-500 hover:text-blue-700 transition-colors duration-300"
        val card = "bg-gray-50 p-4 rounded-lg border border-gray-200"
        val button = "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-all duration-300"

        return@htmlRoute Main(
            "class" to container,
        ) {
            Header("class" to "text-center mb-12") {
                H1 { Fractal("Void Framework — Documentation") }
                P { Fractal("A lightweight Kotlin DSL for building HTML UIs and pages.") }
                Nav("class" to "flex flex-wrap justify-center gap-4 mt-4") {
                    A("href" to "#installation", "class" to link) { Fractal("Installation") }
                    A("href" to "#getting-started", "class" to link) { Fractal("Getting Started") }
                    A("href" to "#routing", "class" to link) { Fractal("Routing") }
                    A("href" to "#html-dsl", "class" to link) { Fractal("HTML DSL") }
                    A("href" to "#kts", "class" to link) { Fractal("KTS") }
                    A("href" to "#middleware", "class" to link) { Fractal("Middleware") }
                    A("href" to "#server", "class" to link) { Fractal("Server") }
                    A("href" to "#examples", "class" to link) { Fractal("Examples") }
                }
            }

            Section("id" to "installation", "class" to section) {
                H2("class" to heading) { Fractal("Installation") }
                P { Fractal("Add the dependencies to your Gradle build:") }
                Pre {
                    Code {
                        Fractal(
                            """
                            plugins {
                                kotlin("jvm") version "2.2.21"
                            }

                            dependencies {
                                implementation("io.jadiefication:void-base:${'$'}version")
                                implementation("io.jadiefication:void-html:${'$'}version")
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "getting-started", "class" to section) {
                H2("class" to heading) { Fractal("Getting Started") }
                P { Fractal("Create your first page with the Void DSL:") }
                Pre {
                    Code {
                        Fractal(
                            """
                            import io.void.generated.Div
                            import io.void.generated.H1
                            import io.void.html.Element

                            fun hello(): Element = Div {
                                H1 { Fractal(\"Hello, Void!\") }
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "routing", "class" to section) {
                H2("class" to heading) { Fractal("Routing") }
                P { Fractal("Define routes using htmlRoute/apiRoute and register them in simpleServer:") }
                Pre {
                    Code {
                        Fractal(
                            """
                            val home = htmlRoute("/", {}) { _ ->
                                Div { H1 { Fractal("Home") } }
                            }

                            val api = apiRoute("/api/ping") { _ ->
                                buildResponse {
                                    status = 200
                                    statusText = "OK"
                                    headers { put("Content-Type", "application/json") }
                                    body = "{\"pong\":true}"
                                }
                            }

                            fun main() {
                                simpleServer {
                                    +home
                                    +api
                                }
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "html-dsl", "class" to section) {
                H2("class" to heading) { Fractal("HTML DSL") }
                P { Fractal("Compose UI using generated strongly-typed elements and Fractal text nodes.") }
                Pre {
                    Code {
                        Fractal(
                            """
                            val page = htmlRoute("/dsl", {}) { _ ->
                                Main("class" to "p-6") {
                                    Header { H1 { Fractal("Title") } }
                                    Section { P { Fractal("Body paragraph") } }
                                    Footer { Small { Fractal("Footer text") } }
                                }
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "features", "class" to section) {
                H2("class" to heading) { Fractal("Features") }
                Div("class" to "grid grid-cols-1 md:grid-cols-2 gap-4") {
                    Article("class" to card) {
                        H3("class" to subheading) { Fractal("Type-Safe HTML DSL") }
                        P { Fractal("Build your UI with a fully type-safe Kotlin DSL") }
                    }
                    Article("class" to card) {
                        H3("class" to subheading) { Fractal("Composable Components") }
                        P { Fractal("Compose elements and fragments to create reusable building blocks") }
                    }
                    Article("class" to card) {
                        H3("class" to subheading) { Fractal("KTS Interactions") }
                        P { Fractal("Attach client-side KTS configs to elements to invoke server-side fragments") }
                    }
                }
            }

            Section("id" to "kts", "class" to section) {
                H2("class" to heading) { Fractal("KTS Demo") }
                P { Fractal("Trigger a KTS route that swaps a target element's content:") }
                Button("class" to button, "id" to "kts-btn") {
                    Fractal("Run KTS Action")
                    kts {
                        on("/docs/kts-hello", Method.POST)
                        target("#kts-target")
                        swap("innerHTML")
                        trigger("click")
                        confirm("Proceed with KTS request?")
                    }
                }
                Div("id" to "kts-target", "class" to card) { Fractal("KTS Response will appear here") }
                Pre {
                    Code {
                        Fractal(
                            """
                            val kts = ktsRoute("/docs/kts-hello") { req, trigger, target ->
                                Div { P { Fractal("Hello") } }
                            }
                            Button("id" to "kts-btn") {
                                Fractal("Run")
                                kts {
                                    on("/docs/kts-hello", Method.POST)
                                    target("#kts-target")
                                    swap("innerHTML")
                                    trigger("click")
                                }
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "middleware", "class" to section) {
                H2("class" to heading) { Fractal("Middleware") }
                P { Fractal("Register before/after relays globally or per-page to implement cross-cutting concerns.") }
                Pre {
                    Code {
                        Fractal(
                            """
                            simpleServer {
                                +relayAfter { result ->
                                    result.fold(onSuccess = { println(it) }, onFailure = { println(it) })
                                    null
                                }
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "server", "class" to section) {
                H2("class" to heading) { Fractal("Server") }
                P { Fractal("Boot a server with simpleServer and add pages:") }
                Pre {
                    Code {
                        Fractal(
                            """
                            fun main() {
                                simpleServer {
                                    +docsHomeRoute
                                    on("/health") GET { _ -> ok("OK") }
                                }
                            }
                            """.trimIndent(),
                        )
                    }
                }
            }

            Section("id" to "examples", "class" to section) {
                H2("class" to heading) { Fractal("Examples") }
                P { Fractal("Explore the test module for comprehensive examples and unit tests.") }
            }

            Footer("class" to "text-center mt-12 text-gray-600") {
                Small { Fractal("© 2025 Void Framework Docs") }
            }
        }
    }

    // If you have a stylesheet resource, you can attach it like in tests.
    // Omitted here to avoid requiring a resource file.

val docsKtsRoute =
    ktsRoute("/docs/kts-hello") { request, trigger, target ->
        Div("class" to "bg-green-50 p-2 rounded") {
            H3 { Fractal("Hello from Docs KTS!") }
            P { Fractal("Trigger ID: ${trigger?.get("id")}") }
            P { Fractal("Target ID: ${target?.get("id")}") }
        }
    }

fun main() {
    // Starts an HTTP server using the same DSL style as the test module.
    val server =
        simpleServer {
            +docsHomeRoute
            +docsKtsRoute

            // Health endpoint using the on("/path") GET style from tests
            on("/health") GET { _ ->
                buildResponse {
                    status = 200
                    statusText = "OK"
                    headers { put("Content-Type", "text/plain") }
                    body = "OK"
                }
            }
        }

    // server is started by simpleServer builder; nothing else to do.
}
