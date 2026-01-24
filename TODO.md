# Void Framework TODO

This document breaks down the evolution plan into actionable, bite‑sized tasks you can convert into GitHub issues and
branches.

Milestone 5 — Router/middleware polish (1–2 weeks)

1. Route groups and scoped middleware: group("/api") { before(Auth) … }. (area/router, kind/feature, 1–2d)
2. Content negotiation helper accepts(Json) { … } else { … }. (area/router, kind/feature, 1d)
3. Static assets: hashed URLs, ETag/If‑None‑Match support. (area/router, kind/feature, 1–2d)
4. Rate limiting middleware (token bucket). (area/router, kind/feature, 1–2d)

Milestone 6 — Performance & caching (1–2 weeks)

1. Data loader layer for N+1 (request‑scoped batching). (area/perf, kind/feature, 2–3d)
2. Cache layer refactor for clarity and testability. (area/perf, kind/refactor, 1–2d)
3. Cache invalidation correctness and regression tests. (area/perf, kind/fix, 1–2d)

Milestone 7 — Tooling and DX (1–2 weeks)

1. CLI: void dev, void build, void analyze, void routes, void state. (area/tooling, kind/feature, 3–4d) → Will be done in [Void-CLI](https://github.com/Void-Framework/Void-CLI)
2. State inspector at /__void in dev showing atoms and patch stream. (area/tooling, kind/feature, 2–3d)
3. Documentation generator from KDoc with runnable examples. (area/tooling, kind/docs, 3–4d)

Migration and compatibility

- Provide adapters between existing Page/DynamicPage and new components. (area/dsl, kind/feature, 2–3d)
- Feature flags (Void.features.*) to gate hydration/state features. (area/tooling, kind/feature, 1d)
- Deprecation plan with warnings; keep old APIs until vNext. (area/dsl, kind/docs, 1d)

Housekeeping & CI

1. CI workflows
    - Build, Test, Lint (Kotlin/JVM + Kotlin/JS), Publish snapshots. (area/tooling, kind/feature, 2–3d)
2. Release process
    - Versioning, changelog, artifacts (Maven). (area/tooling, kind/feature, 2–3d)
3. Qodana rules tuned for DSL files and generated code. (area/tooling, kind/chore, 0.5d)

Suggested GitHub issues (titles + branch names)

- [M3] Gradle ergonomics: tailwindGen, assetsBundle, devServer — feature/gradle-tasks
- [M5] Route groups and content negotiation — feature/router-polish
- [M5] Static assets hashed URLs + ETag — feature/assets-cache
- [M5] Rate limiting middleware — feature/rate-limit
- [M6] Data loader layer — feature/dataloader
- [M7] CLI tooling and state inspector — feature/cli-inspector
- CI: build/test/lint + releases — chore/ci-and-release

Acceptance checklist per feature

- API surface documented with KDoc and in TODO.md issue description.
- Unit/integration tests updated or added.
- Examples adjusted and CI green.
- Backward compatible or guarded by feature flags.

---

Code examples: how to use each feature

Note: These examples illustrate the target developer experience (DX). Some APIs are placeholders to be implemented in
their respective milestones. Examples assume Kotlin on both server and client (Kotlin/JS IR).

- Dev workflow with Kotlin/JS and backend proxy
  ```bash
  # run Kotlin/JS dev server with HMR
  ./gradlew jsBrowserDevelopmentRun
  # backend dev server proxies /assets to http://localhost:8080 (or JS dev server port)
  ./gradlew devServer -Dvoid.env=dev
  ```

Milestone 5 — Router/middleware polish

- Route groups and scoped middleware
  ```kotlin
  group("/api") {
      before(Auth::class)
      get("/me") { ok(json(me())) }
  }
  ```

- Content negotiation helper
  ```kotlin
  get("/profile") {
      accepts(ContentType.Json) { ok(json(profile())) }
          .elseHtml { ok(html(ProfilePage())) }
  }
  ```

- Rate limiting middleware
  ```kotlin
  before(RateLimit::class) // token bucket default policy
  ```

Milestone 6 — Performance & caching

- Data loader layer
  ```kotlin
  val userById = loader<Int, User> { ids -> repo.loadUsers(ids) }
  suspend fun profile(id: Int) = userById.load(id)
  ```

Milestone 7 — Tooling and DX

- CLI commands
  ```bash
  void routes
  void state
  void analyze
  ```

- State inspector
  ```text
  Open /__void in dev to inspect atoms and watch patch streams.
  ```

Milestone 8 — Security defaults

- CSRF protection on mutations
  ```kotlin
  before(CsrfProtect::class)
  post("/update-profile") {
      // CsrfProtect validates token from header/cookie
      ok(json("updated"))
  }
  ```

- Validation derived from Kotlin models
  ```kotlin
  @Serializable data class UserCreate(val name: String, @Email val email: String)
  post("/users") {
      val body = jsonBody<UserCreate>() // validated
      ok(json(userService.create(body)))
  }
  ```
