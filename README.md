<div align="center">

  <img alt="Void logo" src=".github/image.png" width="160" height="160" />
  <h1>Void</h1>
  <p>A minimal Kotlin web framework for building HTML pages and APIs with a tiny HTTP/HTTPS server.</p>

  <p>
    <a href="https://jitpack.io/#Jadiefication/Void"><img alt="JitPack" src="https://jitpack.io/v/Jadiefication/Void.svg"></a>
    <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/kotlin-2.2.21-blue.svg?logo=kotlin"></a>
    <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
    <a href="https://gitpod.io/#https://github.com/Jadiefication/Void"><img alt="Contribute with Gitpod" src="https://img.shields.io/badge/Contribute%20with-Gitpod-908a85?logo=gitpod"></a>
<a href="https://codecov.io/github/Void-Framework/Void" > 
 <img src="https://codecov.io/github/Void-Framework/Void/graph/badge.svg?token=YW4IFKF62X"/> 
 </a>
  </p>
</div>

Void is a small, unopinionated framework you can embed into your app. It provides:

- Simple router with static and dynamic routes
- Middleware (before/after) with priorities
- First-class API endpoints returning `ResponseDTO`
- Minimal HTTP/HTTPS server (no servlet container)
- Bootstrapper for external modules (page decorators, special routes, error handlers)

Quick links

- Security policy: [SECURITY.md](SECURITY.md)
- Roadmap/TODO: [TODO.md](TODO.md)
- Contributing guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Code of Conduct: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- Support: [SUPPORT.md](SUPPORT.md)
- License: MIT ([LICENSE](LICENSE))
- Wiki: [DeepWiki](https://deepwiki.com/Void-Framework/Void)

## Tech Stack

- **Language:** [Kotlin 2.2.21](https://kotlinlang.org/)
- **Build System:** [Gradle 8.x+](https://gradle.org/) (Kotlin DSL)
- **Minimum Java:** 8 (for library consumers), 21 (for building the project)
- **Frameworks:** kotlinx-serialization (JSON, CBOR, Protobuf), SLF4J
- **Distribution:** [JitPack](https://jitpack.io/)

## Project Structure

- `void-base/`: The core framework library.
  - `src/main/kotlin/io/voidx/`: Core server, router, and DTO logic.
  - `src/main/kotlin/io/voidx/bootstrap/`: Module bootstrapping system.
- `docs/`: Documentation module containing a built-in server and static site generator.
  - `src/main/kotlin/io/voidx/docs/`: Documentation server and export logic.
- `gradle/`: Gradle wrapper and version catalogs.

## Get started

### Requirements

- JDK 21+ (to build)
- JDK 8+ (to run)

### Installation (JitPack)

Add the repository and dependency:

```kotlin
repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Jadiefication:Void:VERSION")
}
```

### Hello, Void

Create a minimal server with one text route and one JSON route:

```kotlin
fun main() {
    val server = io.voidx.simpleServer {
      io.voidx.page.route("/") {
        GET {
          io.voidx.dto.ok("Hello, Void!", mutableMapOf("Content-Type" to "text/plain"))
        }
      }

      // JSON route
      io.voidx.page.route("/api/health") {
        GET {
          io.voidx.dto.buildResponse<String> {
            status = 200
            statusText = "OK"
            headers["Content-Type"] = "application/json"
            body = "{\"status\":\"ok\"}"
          }
        }
      }
    }
}
```

Then open [http://localhost:8080](http://localhost:8080)

## Commands & Scripts

The project uses Gradle for all common tasks:

- `./gradlew build`: Build all modules.
- `./gradlew test`: Run all tests.
- `./gradlew jacocoRootReport`: Generate an aggregate test coverage report (found in `build/reports/jacoco/jacocoRootReport/html/index.html`).
- `./gradlew ktlintCheck`: Run linting checks.

## Configuration & Env Vars

Void is designed to be unopinionated and doesn't rely on specific environment variables by default.

## Principles

#### Unopinionated
Void doesn’t force a particular logging, DI, templating, or persistence stack. Compose apps using functions and small DSLs. Middleware integrates via a simple interception mechanism.

#### Asynchronous
Request handling uses Kotlin coroutines under the hood to keep I/O non-blocking with a straightforward API.

#### Testable
Pages and routers can be constructed and invoked in tests without spinning up external containers. You can exercise handlers directly or run the tiny server in integration tests.

## Documentation

Core entry points:

- `io.voidx.router.router { }` — Create and configure a router.
- `io.voidx.page.route("/path") { GET { ... } }` — Define a page/route.
- `io.voidx.Server` — Start HTTP/HTTPS servers.
- `io.voidx.middleware.Relay` — Middleware base for `relayBefore` / `relayAfter`.
- `io.voidx.bootstrap.Bootstrap` — Module bootstrapper and DX helpers.

### External modules via Bootstrap

Void exposes a lightweight bootstrapper so external modules can hook into internals:

- **Page decorators:** Run once when a page is added.
- **Special routes:** Prioritized pre-dispatch handlers.
- **Error handlers:** Observe/handle errors produced by the router.

Example module:

```kotlin
class MyModule : io.voidx.bootstrap.Bootstrap.Module {
    override fun onRouterCreated(ctx: io.voidx.bootstrap.Bootstrap.Context) {
        ctx.addRoute(
            io.voidx.page.route("/hello") { GET { io.voidx.dto.ok("hi", mutableMapOf("Content-Type" to "text/plain")) } }
        )
    }
}
```

Register modules via `META-INF/services/io.voidx.bootstrap.Bootstrap$Module`.

## Testing

The test suite is authoritative and aims for high coverage.
- Run tests: `./gradlew test`
- Coverage: `./gradlew jacocoRootReport`

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## License

[MIT](LICENSE) — © 2025 Jadiefication
