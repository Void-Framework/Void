// Uses softer neutrals, better spacing, cleaner typography, and simpler layouts

package io.voidx.docs

import io.voidx.dto.buildResponse
import io.voidx.dto.headers
import io.voidx.html.generated.*
import io.voidx.html.metadata.metadata
import io.voidx.html.page.html
import io.voidx.html.page.metadata
import io.voidx.page.route
import io.voidx.simpleServer

val docsHomeRoute =
    route("/") {
        html({ title = "Docs" }) {

            // Base styles
            val container = "min-h-screen bg-slate-50 text-slate-800 antialiased"
            val inner = "max-w-4xl mx-auto px-6 py-14"

            // Hero
            val hero =
                "relative overflow-hidden rounded-3xl bg-gradient-to-br from-slate-900 via-slate-800 to-slate-700 shadow-2xl mb-14"
            val heroInner = "px-10 py-14"
            val heroTitle = "text-5xl md:text-6xl font-black tracking-tight text-white"
            val heroSubtitle = "mt-4 text-xl text-slate-300 max-w-2xl"
            val heroNav = "mt-8 flex flex-wrap gap-3"
            val button =
                "inline-flex items-center gap-2 bg-white/10 backdrop-blur-sm text-white px-4 py-2 rounded-lg hover:bg-white/20 transition"

            // Sections
            val section = "bg-white/80 backdrop-blur-sm rounded-2xl shadow-sm ring-1 ring-slate-200 p-8 mb-10"
            val heading = "text-3xl font-bold text-slate-900 tracking-tight mb-4"
            val paragraph = "prose prose-slate max-w-none"

            val codeBlock =
                "bg-slate-900/90 text-slate-100 p-4 rounded-xl overflow-x-auto text-sm font-mono border border-slate-800"

            val card =
                "bg-white/60 backdrop-blur-sm p-6 rounded-xl border border-slate-200 hover:shadow-md transition-all"

            val link = "text-slate-700 hover:text-slate-900 font-medium transition-colors"

            val footer = "text-center mt-16 py-6 text-slate-500 text-sm border-t border-slate-200"

            Main("class" to container) {
                Div("class" to inner) {

                    // HERO
                    Div("class" to hero) {
                        Div("class" to heroInner) {
                            H1("class" to heroTitle) { +"Void Framework Documentation" }
                            P("class" to heroSubtitle) {
                                +"A lightweight Kotlin DSL for building UIs, API routes, and full pages — clean, fast, and type‑safe."
                            }
                            Nav("class" to heroNav) {
                                listOf(
                                    "Install" to "#installation",
                                    "Start" to "#getting-started",
                                    "Routing" to "#routing",
                                    "HTML DSL" to "#html-dsl",
                                    "Server" to "#server",
                                    "Examples" to "#examples",
                                ).forEach { (t, href) ->
                                    A("href" to href, "class" to button) { +t }
                                }
                            }
                        }
                    }

                    // TOC
                    Section("class" to section) {
                        H2("class" to heading) { +"Contents" }
                        Ul("class" to "space-y-2") {
                            listOf(
                                "Installation" to "#installation",
                                "Getting Started" to "#getting-started",
                                "Concepts" to "#concepts",
                                "Routing" to "#routing",
                                "Dynamic Routes" to "#dynamic-routes",
                                "HTML DSL" to "#html-dsl",
                                "Middleware" to "#middleware",
                                "Server" to "#server",
                                "Metadata" to "#metadata",
                                "Deployment" to "#deployment",
                                "Examples" to "#examples",
                            ).forEach { (t, href) ->
                                Li { A("href" to href, "class" to link) { +t } }
                            }
                        }
                    }

                    // INSTALLATION
                    Section("id" to "installation", "class" to section) {
                        H2("class" to heading) { +"Installation" }
                        P("class" to paragraph) { +"Add these dependencies to your Gradle build:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    plugins {
                                        kotlin("jvm") version "2.2.21"
                                    }

                                    dependencies {
                                        implementation("com.github.jadiefication:void-base:\${'$'}version")
                                        implementation("com.github.jadiefication:void-html:\${'$'}version")
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // GETTING STARTED
                    Section("id" to "getting-started", "class" to section) {
                        H2("class" to heading) { +"Getting Started" }
                        P("class" to paragraph) { +"Create your first page:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    fun hello(): Element = Div {
                                        H1 { +"Hello, Void!" }
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // CONCEPTS
                    Section("id" to "concepts", "class" to section) {
                        H2("class" to heading) { +"Core Concepts" }
                        Ul("class" to "prose prose-slate max-w-none") {
                            Li {
                                Strong { +"Page" }
                                +": returns a ResponseDTO."
                            }
                            Li {
                                Strong { +"Route" }
                                +": maps a path to a handler or page."
                            }
                            Li {
                                Strong { +"Middleware" }
                                +": before/after pipeline hooks."
                            }
                            Li {
                                Strong { +"Metadata" }
                                +": controls <head> tags and assets."
                            }
                        }
                    }

                    // ROUTING
                    Section("id" to "routing", "class" to section) {
                        H2("class" to heading) { +"Routing" }
                        P("class" to paragraph) { +"Define your first route:" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    val home = route("/") {
                                        GET { _ -> html { H1 { +"Home" } } }
                                    }

                                    fun main() {
                                        simpleServer { route(home) }
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // DYNAMIC ROUTES
                    Section("id" to "dynamic-routes", "class" to section) {
                        H2("class" to heading) { +"Dynamic Routes" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    val search = route("/search") {
                                        GET { _ ->
                                            val q = docsHomeRoute.queries["q"] ?: ""
                                            html { H1 { +"Results for: \${'$'}q" } }
                                        }
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // HTML DSL
                    Section("id" to "html-dsl", "class" to section) {
                        H2("class" to heading) { +"HTML DSL" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    val page = route("/dsl") {
                                        GET { _ ->
                                            html {
                                                Main("class" to "p-6") {
                                                    H1 { +"Title" }
                                                    P { +"Paragraph" }
                                                }
                                            }
                                        }
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // SERVER
                    Section("id" to "server", "class" to section) {
                        H2("class" to heading) { +"Server" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    fun main() {
                                        simpleServer {
                                            route(docsHomeRoute)
                                        }
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // METADATA
                    Section("id" to "metadata", "class" to section) {
                        H2("class" to heading) { +"Metadata" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    docsHomeRoute.metadata = metadata(docsHomeRoute) {
                                        title = "Docs — Void"
                                        description = "My docs"
                                        externalCss = mutableListOf("/assets/main.css")
                                    }
                                    """.trimIndent()
                            }
                        }
                    }

                    // DEPLOYMENT
                    Section("id" to "deployment", "class" to section) {
                        H2("class" to heading) { +"Deployment" }
                        Pre("class" to codeBlock) {
                            Code {
                                +
                                    """
                                    fun main() = io.voidx.docs.main() // build/pages/index.html
                                    """.trimIndent()
                            }
                        }
                    }

                    // EXAMPLES
                    Section("id" to "examples", "class" to section) {
                        H2("class" to heading) { +"Examples" }
                        P("class" to paragraph) { +"Check the test modules for real examples." }
                    }

                    // FOOTER
                    Footer("class" to footer) { Small { +"© 2025 Void Framework Docs" } }
                }
            }
        }
    }

fun main() {
    simpleServer {
        route(docsHomeRoute)
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
