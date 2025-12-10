<div align="center">

  <img alt="Void logo" src=".github/image.png" width="160" height="160" />
  <h1>Void</h1>
  <p>A minimal Kotlin web framework for building HTML pages and APIs with a tiny HTTP/HTTPS server.</p>

  <p>
    <a href="https://jitpack.io/#Jadiefication/Void"><img alt="JitPack" src="https://jitpack.io/v/Jadiefication/Void.svg"></a>
    <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/kotlin-2.2.21-blue.svg?logo=kotlin"></a>
    <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
    <a href="https://gitpod.io/#https://github.com/Jadiefication/Void"><img alt="Contribute with Gitpod" src="https://img.shields.io/badge/Contribute%20with-Gitpod-908a85?logo=gitpod"></a>
  </p>
</div>

Void is a small, unopinionated framework you can embed into your app. It provides:

- Simple router with static and dynamic routes
- Middleware (before/after) with priorities
- First-class API endpoints returning `ResponseDTO`
- Minimal HTTP/HTTPS server (no servlet container)
- Bootstrapper for external modules (page decorators, special routes, error handlers)

Quick links

- Security policy: SECURITY.md
- Roadmap/TODO: TODO.md
- Contributing guide: CONTRIBUTING.md
- Code of Conduct: CODE_OF_CONDUCT.md
- Support: SUPPORT.md
- License: MIT (LICENSE)

## Get started

Requirements

- Java 8+
- Kotlin 2.2.21 (Gradle Kotlin DSL recommended)

### Installation (JitPack)

Add the repository and dependency:

```kotlin
repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Jadiefication:Void:<version>")
}
```

### Hello, Void

Create a minimal server with one text route and one JSON route (no imports in snippet to keep it copy‑paste friendly):

```kotlin
fun main() {
    val r = io.voidx.router.router {
        // Simple text route
        addRoute(
            io.voidx.page.route("/") {
                GET {
                    io.voidx.dto.ok(
                        "Hello, Void!",
                        mutableMapOf("Content-Type" to "text/plain"),
                    )
                }
            },
        )

        // JSON route
        addRoute(
            io.voidx.page.route("/api/health") {
                GET {
                    io.voidx.dto.buildResponse<String> {
                        status = 200
                        statusText = "OK"
                        headers["Content-Type"] = "application/json"
                        body = "{\"status\":\"ok\"}"
                    }
                }
            },
        )
    }

    val server = io.voidx.Server(r)
    server.startHTTPServer(8080)
}
```

Then open http://localhost:8080

## Principles

#### Unopinionated

Void doesn’t force a particular logging, DI, templating, or persistence stack. Compose apps using functions and small
DSLs. Middleware integrates via a simple interception mechanism.

#### Asynchronous

Request handling uses Kotlin coroutines under the hood to keep I/O non-blocking with a straightforward API.

#### Testable

Pages and routers can be constructed and invoked in tests without spinning up external containers. You can exercise
handlers directly or run the tiny server in integration tests.

## Documentation

Until a dedicated site is available, see this README and the test sources for examples. Core entry points:

- `io.voidx.router.router { }` — create and configure a router
- `io.voidx.page.route("/path") { GET { ... } }` — define a page/route
- `io.voidx.Server` — start HTTP/HTTPS servers
- `io.voidx.middleware.relayBefore / relayAfter` — global middleware
- `io.voidx.bootstrap.Bootstrap` — module bootstrapper and DX helpers

### External modules via Bootstrap

Void exposes a lightweight bootstrapper so external modules can hook into internals without ad‑hoc globals:

- Page decorators: run once when a page is added (e.g., register assets, inject metadata)
- Special routes: prioritized pre‑dispatch handlers that can return a response and short‑circuit normal routing
- Error handlers: observe/handle errors produced by the router

Example module (using fully‑qualified names to keep the snippet standalone):

```kotlin
class MyModule : io.voidx.bootstrap.Bootstrap.Module {
    override fun onRouterCreated(ctx: io.voidx.bootstrap.Bootstrap.Context) {
        // Add a route
        ctx.addRoute(
            io.voidx.page.route("/hello") { GET { io.voidx.dto.ok("hi", mutableMapOf("Content-Type" to "text/plain")) } },
        )

        // Register a high‑priority special route
        ctx.addSpecialRoute(priority = 100) { req, query, client ->
            if (req.headers["X-Special"] == "1") io.voidx.dto.ok("special!") else null
        }

        // Decorate pages when they are registered
        ctx.addPageDecorator { page, router ->
            // e.g., register static resources or annotate page metadata
        }

        // Observe router errors
        ctx.addErrorHandler { request, throwable ->
            // log/telemetry
        }
    }
}
```

Modules can be discovered automatically using Java ServiceLoader by adding a file:

```
META-INF/services/io.voidx.bootstrap.Bootstrap$Module
```

whose contents are the fully‑qualified class name, e.g. `com.example.MyModule`.

### Serving static assets from the classpath

From a bootstrap module you can expose resources packaged within your JAR:

```kotlin
class Assets : io.voidx.bootstrap.Bootstrap.Module {
    override fun onRouterCreated(ctx: io.voidx.bootstrap.Bootstrap.Context) {
        ctx.serveClasspathResources("/static", "public")     // serves classpath: public/** under /static/**
        ctx.serveClasspathFile("/elements.json", "elements.json") // serves a single file
    }
}
```

### HTML/KTS integration

Legacy `io.voidx.util.HtmlIntegration` is deprecated and kept as a no‑op shim. HTML/CSS/JS and KTS integration should
be provided by an external extension module using the Bootstrap hooks described above.

## Testing philosophy

- The test suite is authoritative and aims for very high (ideally 100%) coverage.
- When a test reveals a mismatch, prefer updating the framework code to satisfy the test rather than deleting or weakening the test. Tests exist to capture supported behavior and edge cases; removing cases is discouraged.

## Reporting Issues / Support

- File bugs and feature requests using GitHub Issues.
- For questions, Discussions or StackOverflow (tag: kotlin) are recommended.

## Reporting Security Vulnerabilities

Please follow the process in SECURITY.md for private disclosure.

## Contributing

We welcome contributions of all kinds. Before large changes, please open an issue to discuss direction. Keep PRs
focused; add tests or examples where appropriate.

## License

MIT — see LICENSE for details.
