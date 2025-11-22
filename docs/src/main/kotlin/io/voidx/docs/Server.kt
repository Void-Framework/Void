package io.voidx.docs

import io.voidx.dto.buildResponse
import io.voidx.dto.headers
import io.voidx.html.generated.*
import io.voidx.html.metadata.metadata
import io.voidx.html.page.html
import io.voidx.html.page.metadata
import io.voidx.page.route
import io.voidx.simpleServer

/**
 * A modern, clean-looking Docs homepage built using Tailwind and Void DSL.
 */
val docsHomeRoute =
    route("/") {
        html({
            title = "Docs"
        }) {

            // Global layout classes
            val container = "min-h-screen bg-gray-50 text-gray-800 antialiased"
            val inner = "max-w-6xl mx-auto px-6 py-12"

            // Hero section
            val hero =
                "relative overflow-hidden rounded-3xl bg-gradient-to-r from-indigo-600 via-purple-600 to-pink-500 shadow-xl mb-12"
            val heroInner = "rounded-3xl bg-white/95 backdrop-blur-md px-10 py-12 md:px-16 md:py-16"
            val heroTitle = "text-5xl md:text-6xl font-extrabold tracking-tight text-white drop-shadow"
            val heroSubtitle = "mt-4 text-lg md:text-xl text-white/90"
            val heroNav = "mt-8 flex flex-wrap gap-3"
            val button =
                "inline-flex items-center gap-2 bg-indigo-600 text-white px-4 py-2 rounded-lg hover:bg-indigo-700 active:bg-indigo-800 shadow-md transition duration-200"

            // Section styling
            val section = "bg-white rounded-2xl shadow ring-1 ring-gray-100 p-8 mb-8"
            val heading = "text-3xl font-bold text-gray-900 mb-4"
            val subheading = "text-lg font-semibold text-gray-800 mb-2"
            val link = "text-indigo-600 hover:text-indigo-700 underline underline-offset-4 transition-colors duration-200"
            val card = "bg-gray-50 p-6 rounded-xl border border-gray-200 hover:shadow-lg transition-shadow duration-300"
            val codeBlock = "bg-gray-900 text-gray-100 p-4 rounded-lg overflow-x-auto text-sm font-mono"
            val footer = "text-center mt-16 py-6 text-gray-500 text-sm border-t border-gray-200"

            Main("class" to container) {
                Div("class" to inner) {
                    // Hero
                    Div("class" to hero) {
                        Div("class" to heroInner) {
                            H1("class" to heroTitle) { +"Void Framework — Documentation" }
                            P("class" to heroSubtitle) { +"A lightweight Kotlin DSL for building HTML UIs, API endpoints, and pages." }
                            Nav("class" to heroNav) {
                                listOf(
                                    "Installation" to "#installation",
                                    "Getting Started" to "#getting-started",
                                    "Routing" to "#routing",
                                    "Dynamic Routes" to "#dynamic-routes",
                                    "HTML DSL" to "#html-dsl",
                                    "Middleware" to "#middleware",
                                    "Server" to "#server",
                                    "Metadata" to "#metadata",
                                    "Errors" to "#error-handling",
                                    "Testing" to "#testing",
                                    "Deployment" to "#deployment",
                                    "Examples" to "#examples",
                                ).forEach { (text, href) ->
                                    A("href" to href, "class" to button) { +text }
                                }
                            }
                        }
                    }

                    // Table of Contents
                    Section("class" to section) {
                        H2("class" to heading) { +"Table of Contents" }
                        Ul {
                            listOf(
                                "Installation" to "#installation",
                                "Getting Started" to "#getting-started",
                                "Concepts" to "#concepts",
                                "Routing" to "#routing",
                                "Dynamic Routes" to "#dynamic-routes",
                                "HTML DSL" to "#html-dsl",
                                "Middleware" to "#middleware",
                                "Server" to "#server",
                                "Metadata & Assets" to "#metadata",
                                "Error Handling" to "#error-handling",
                                "Testing" to "#testing",
                                "Deployment" to "#deployment",
                                "FAQ" to "#faq",
                            ).forEach { (t, href) -> Li { A("href" to href, "class" to link) { +t } } }
                        }
                    }

                    // Installation
                    Section("id" to "installation", "class" to section) {
                        H2("class" to heading) { +"Installation" }
                        P { +"Add the dependencies to your Gradle build:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        plugins {
                                            kotlin("jvm") version "2.2.21"
                                        }

                                        dependencies {
                                            implementation("com.github.jadiefication:void-base:${'$'}version")
                                            implementation("com.github.jadiefication:void-html:${'$'}version")
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Getting Started
                    Section("id" to "getting-started", "class" to section) {
                        H2("class" to heading) { +"Getting Started" }
                        P { +"Create your first page with the Void DSL:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        import io.voidx.html.generated.Div
                                        import io.voidx.html.generated.H1
                                        import io.voidx.html.Element

                                        fun hello(): Element = Div {
                                            H1 { +"Hello, Void!" }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Concepts
                    Section("id" to "concepts", "class" to section) {
                        H2("class" to heading) { +"Core Concepts" }
                        Ul {
                            Li { Strong { +"Page" }; +": a unit that produces a ResponseDTO (HTML or raw)." }
                            Li { Strong { +"Route" }; +": path mapped to a Page or PageHandler (e.g. /, /api/ping)." }
                            Li { Strong { +"Middleware" }; +": before/after relays that can short‑circuit or observe responses." }
                            Li { Strong { +"Metadata" }; +": title, description, and asset links injected into <head>." }
                        }
                    }

                    // Routing
                    Section("id" to "routing", "class" to section) {
                        H2("class" to heading) { +"Routing" }
                        P { +"Define routes with route(\"/path\") and register them in simpleServer:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        val home = route("/") {
                                            GET { _ ->
                                                html {
                                                    Div { H1 { +"Home" } }
                                                }
                                            }
                                        }

                                        val api = route("/api/ping") {
                                            GET { _ ->
                                                buildResponse {
                                                    status = 200
                                                    statusText = "OK"
                                                    headers { put("Content-Type", "application/json") }
                                                    body = "{\"pong\":true}"
                                                }
                                            }
                                        }

                                        fun main() {
                                            simpleServer {
                                                route(home)
                                                route(api)
                                            }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Dynamic Routes
                    Section("id" to "dynamic-routes", "class" to section) {
                        H2("class" to heading) { +"Dynamic Routes & Query Params" }
                        P { +"Use typed handlers and read queries from the bound Page instance:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        val search = route("/search") {
                                            GET { _ ->
                                                // Inside handlers, the page is bound: use docsHomeRoute.queries or your own page ref
                                                val q = docsHomeRoute.queries["q"] ?: ""
                                                html { Div { H1 { +"Results for: ${'$'}q" } } }
                                            }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // HTML DSL
                    Section("id" to "html-dsl", "class" to section) {
                        H2("class" to heading) { +"HTML DSL" }
                        P { +"Compose UI using generated strongly-typed elements and unaryPlus (+) for text nodes." }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        val page = route("/dsl") {
                                            GET { _ ->
                                                html {
                                                    Main("class" to "p-6") {
                                                        Header { H1 { +"Title" } }
                                                        Section { P { +"Body paragraph" } }
                                                        Footer { Small { +"Footer text" } }
                                                    }
                                                }
                                            }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Features
                    Section("id" to "features", "class" to section) {
                        H2("class" to heading) { +"Features" }
                        Div("class" to "grid grid-cols-1 md:grid-cols-2 gap-6") {
                            listOf(
                                "Type-Safe HTML DSL" to "Build your UI with a fully type-safe Kotlin DSL",
                                "Composable Components" to "Compose elements and fragments to create reusable building blocks",
                                // KTS interactions disabled for static export
                            ).forEach { (title, desc) ->
                                Article("class" to card) {
                                    H3("class" to subheading) { +title }
                                    if (desc.isNotEmpty()) P { +desc }
                                }
                            }
                        }
                    }

                    // KTS demo removed for static deployment

                    // Middleware
                    Section("id" to "middleware", "class" to section) {
                        H2("class" to heading) { +"Middleware" }
                        P { +"Register before/after relays globally or per-page to implement cross-cutting concerns." }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        simpleServer {
                                            +relayAfter { result ->
                                                result.fold(onSuccess = { println(it) }, onFailure = { println(it) })
                                                null
                                            }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Server
                    Section("id" to "server", "class" to section) {
                        H2("class" to heading) { +"Server" }
                        P { +"Boot a server with simpleServer and add pages:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        fun main() {
                                            simpleServer {
                                                route(docsHomeRoute)
                                                on("/health") GET { _ -> ok("OK") }
                                            }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Metadata
                    Section("id" to "metadata", "class" to section) {
                        H2("class" to heading) { +"Metadata & Assets" }
                        P { +"Customize <head> tags and include external CSS/JS:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        // Assign metadata to a page
                                        docsHomeRoute.metadata = metadata(docsHomeRoute) {
                                            title = "Docs — Void"
                                            description = "My app docs"
                                            externalCss = mutableListOf("/assets/styles.css")
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Error Handling
                    Section("id" to "error-handling", "class" to section) {
                        H2("class" to heading) { +"Error Handling" }
                        P { +"Provide custom pages for 404 and exceptions via notFoundPage/exceptionPage." }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        val notFound = notFoundPage {
                                            buildResponse { status = 404; statusText = "Not Found"; body = "Missing" }
                                        }
                                        val onError = exceptionPage {
                                            buildResponse { status = 500; statusText = "Error"; body = exception.message ?: "error" }
                                        }
                                        """.trimIndent()
                            }
                        }
                    }

                    // Testing
                    Section("id" to "testing", "class" to section) {
                        H2("class" to heading) { +"Testing" }
                        P { +"Use the test modules to validate routes and HTML generation. See void-base and void-html tests." }
                    }

                    // Deployment
                    Section("id" to "deployment", "class" to section) {
                        H2("class" to heading) { +"Deployment" }
                        P { +"Export static pages for GitHub Pages using the docs Export.kt utility:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                """
                                        // In docs module
                                        fun main() = io.voidx.docs.main() // writes build/pages/index.html
                                        """.trimIndent()
                            }
                        }
                    }

                    // Examples
                    Section("id" to "examples", "class" to section) {
                        H2("class" to heading) { +"Examples" }
                        P { +"Explore the test modules for comprehensive examples and unit tests." }
                    }

                    // FAQ
                    Section("id" to "faq", "class" to section) {
                        H2("class" to heading) { +"FAQ" }
                        Div("class" to "grid gap-4") {
                            Article("class" to card) {
                                H3("class" to subheading) { +"Is Tailwind required?" }
                                P { +"No. Classes are optional; you can ship your own CSS via Metadata.externalCss." }
                            }
                            Article("class" to card) {
                                H3("class" to subheading) { +"Can I return JSON?" }
                                P { +"Yes. Use buildResponse and set Content-Type to application/json." }
                            }
                        }
                    }

                    // Footer
                    Footer("class" to footer) {
                        Small { +"© 2025 Void Framework Docs" }
                    }
                }
            }
        }
    }

fun main() {
    // Starts an HTTP server using the same DSL style as the test module.
    val server =
        simpleServer {
            route(docsHomeRoute)

            // Health endpoint
            route("/health") {
                GET {
                    buildResponse {
                        status = 200
                        statusText = "OK"
                        headers { put("Content-Type", "text/plain") }
                        body = "OK"
                    }
                }
            }
        }
}
