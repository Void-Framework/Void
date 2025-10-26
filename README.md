<div align="center">

  <img alt="Void logo" src=".github/image.png" width="160" height="160" />
  <h1>Void</h1>
  <p>A minimal Kotlin web framework for building HTML pages and APIs with a tiny HTTP/HTTPS server.</p>

  <p>
    <a href="https://jitpack.io/#Jadiefication/Void"><img alt="JitPack" src="https://jitpack.io/v/Jadiefication/Void.svg"></a>
    <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/kotlin-2.2.10-blue.svg?logo=kotlin"></a>
    <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
    <a href="https://gitpod.io/#https://github.com/Jadiefication/Void"><img alt="Contribute with Gitpod" src="https://img.shields.io/badge/Contribute%20with-Gitpod-908a85?logo=gitpod"></a>
  </p>
</div>

Void is a small, unopinionated framework you can embed into your app. It provides:

- Type-safe HTML DSL with generated elements (io.void.generated)
- Simple router with static, dynamic, and KTS interaction routes
- Middleware (before/after) with priorities
- First-class API endpoints returning ResponseDTO
- Minimal HTTP/HTTPS server (no servlet container)
- Optional per-page Tailwind CSS extraction at runtime
- In-memory page caching via @Cacheable

Quick links
- Security policy: SECURITY.md
- Roadmap/TODO: TODO.md
- License: MIT (LICENSE)

## Get started

Requirements
- Java 17+
- Kotlin 2.2.10 (Gradle Kotlin DSL recommended)

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
Create a minimal server with one HTML route and one API route:

```kotlin
import io.void.router.router
import io.void.html.page.htmlRoute
import io.void.html.page.apiRoute
import io.void.generated.Div
import io.void.html.Fractal
import io.void.server.server
import io.void.dto.http.ok

fun main() {
    val r = router {
        +htmlRoute("/", { title = "Home" }) { _ ->
            Div("class" to "p-6 text-xl") { Fractal("Hello, Void!") }
        }
        +apiRoute("/api/health") { _ ->
            ok("ok", mutableMapOf("Content-Type" to "text/plain"))
        }
    }

    server {
        router = r
        port = 8080
        routeToHTTPS = false
    }
}
```

Then open http://localhost:8080.

## Principles

#### Unopinionated
Void doesn’t force a particular logging, DI, templating, or persistence stack. Compose apps using functions and small DSLs. Middleware integrates via a simple interception mechanism.

#### Asynchronous
Request handling uses Kotlin coroutines under the hood to keep I/O non-blocking with a straightforward API.

#### Testable
Pages and routers can be constructed and invoked in tests without spinning up external containers. You can exercise handlers directly or run the tiny server in integration tests.

## Documentation
Until a dedicated site is available, see this README and the test module for examples. Core entry points:
- io.void.router.router { }
- io.void.html.page.htmlRoute / apiRoute / dynamicHtmlRoute / dynamicApiRoute
- io.void.server.server { }

## Reporting Issues / Support
- File bugs and feature requests using GitHub Issues.
- For questions, Discussions or StackOverflow (tag: kotlin) are recommended.

## Reporting Security Vulnerabilities
Please follow the process in SECURITY.md for private disclosure.

## Contributing
We welcome contributions of all kinds. Before large changes, please open an issue to discuss direction. Keep PRs focused; add tests or examples where appropriate.

## License
MIT — see LICENSE for details.
