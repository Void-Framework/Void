# Void Framework TODO

This document breaks down the evolution plan into actionable, bite‑sized tasks you can convert into GitHub issues and
branches.

Milestone 1 — KTS experience and routing foundation

1. Response helpers and content DSL
    - fileDownload() helper mapping to ResponseDTO. (area/dsl, kind/feature, 0.5–1d)
2. Error handling improvements
    - Enrich IExceptionPage with request id, route info, middleware chain, headers. (area/router, kind/feature, 1–2d)
    - Structured logging with traceId header. (area/tooling, kind/feature, 1d)
3. Validation at build time (KSP groundwork)
    - KSP processor skeleton scanning router {} blocks. (area/tooling, kind/feature, 2–3d)
    - Emit warnings for duplicate paths/unreachable routes; generate routes.json. (area/tooling, kind/feature, 2d)
4. Gradle ergonomics — moved to Milestone 3
    - See Milestone 3 → Bundler/dev workflow (tailwindGen, assetsBundle, devServer).
5. TailwindGen manifest
    - Extend TailwindGen to emit manifest with hashed filenames. (area/tooling, kind/feature, 1d)

Milestone 2 — Server‑authoritative reactive state (2–3 weeks)

1. Reactive store primitives
    - Define Atom<T>, AtomId<T>, Store with StateFlow. (area/state, kind/feature, 2d)
    - Mutex per atom for concurrency; version counter per atom. (area/state, kind/feature, 1d)
2. Transport: WebSocket state channel
    - ws("/state") endpoint with SUBSCRIBE and PATCH frames (JSON). (area/state, kind/feature, 2–3d)
    - Initial full snapshot on subscribe; incremental patch thereafter. (area/state, kind/feature, 2d)
3. Diffing/patching
    - Simple JSON diff (replace/add/remove) to start. (area/state, kind/feature, 2d)
4. Hydration bridge
    - Embed initial snapshots into SSR HTML window.__VOID_STATE__. (area/state, area/dsl, kind/feature, 1–2d)
5. Sample feature
    - Implement Counter/Cart demo with an Atom and live updates. (area/state, kind/feature, 1d)
6. Persistence (optional in M2)
    - In‑memory store abstraction; plug‑point for Redis/SQL later. (area/state, kind/feature, 1d)

Milestone 3 — Client DSL: Kotlin/JS IR + RPC (3–4 weeks)

1. Kotlin/JS client runtime (@void/client-kt)
    - atom(id), subscribe, useAtom hook, rpc helper; tiny Kotlin/JS runtime targeting modern ESM. (area/client,
      kind/feature, 3d)
    - SSE fallback for state if WS unavailable (optional). (area/client, kind/feature, 2d)
2. RPC surface and codegen
    - Define @Rpc annotation in Kotlin. (area/tooling, kind/feature, 0.5d)
    - KSP emits Kotlin/JS client stubs and shares kotlinx.serialization models; no JS/TS code. (area/tooling,
      kind/feature, 3–4d)
    - Runtime validation via shared serializers (optional JSON schema export for docs only). (area/tooling,
      kind/feature, 2–3d)
3. Bundler/dev workflow
    - Kotlin/JS IR with browser target; Gradle tasks jsBrowserDevelopmentRun/jsBrowserDistribution. (area/tooling,
      kind/feature, 2–3d)
    - Backend dev server proxies /assets to the Kotlin/JS dev server in dev (HMR via Gradle tasks). (area/tooling,
      kind/feature, 2d)
    - Gradle ergonomics: tailwindGen, assetsBundle (placeholder), devServer (proxy). (area/tooling, kind/feature, 1–2d)
    - Tailwind pipeline rewrite with ability to exclude KTS and Tailwind; config switches. (area/tooling, kind/feature, 2d)
4. SSR + hydration demo
    - Server renders HTML; client hydrates a mounted component using useAtom. (area/client, kind/feature, 2d)

Milestone 4 — Server‑Driven UI primitives (1–2 weeks)

1. HTMX‑style attributes support in HTML builder (hx-get, hx-swap, hx-push-url). (area/dsl, kind/feature, 2d)
2. Fragment endpoints returning partial HTML + headers for triggers. (area/router, kind/feature, 2d)
3. Swap strategy utilities and examples. (area/dsl, kind/docs, 1d)

Milestone 5 — Router/middleware polish (1–2 weeks)

1. Route groups and scoped middleware: group("/api") { before(Auth) … }. (area/router, kind/feature, 1–2d)
2. Content negotiation helper accepts(Json) { … } else { … }. (area/router, kind/feature, 1d)
3. Static assets: hashed URLs, ETag/If‑None‑Match support. (area/router, kind/feature, 1–2d)
4. Rate limiting middleware (token bucket). (area/router, kind/feature, 1–2d)

Milestone 6 — Performance & caching (1–2 weeks)

1. SSR fragment/page cache with TTL and vary by user/locale. (area/perf, kind/feature, 2–3d)
2. Data loader layer for N+1 (request‑scoped batching). (area/perf, kind/feature, 2–3d)
3. Streaming HTML (chunked) support. (area/perf, kind/feature, 2–3d)
4. Cache layer refactor for clarity and testability. (area/perf, kind/refactor, 1–2d)
5. Cache invalidation correctness and regression tests. (area/perf, kind/fix, 1–2d)

Milestone 7 — Tooling and DX (1–2 weeks)

1. CLI: void dev, void build, void analyze, void routes, void state. (area/tooling, kind/feature, 3–4d)
2. State inspector at /__void in dev showing atoms and patch stream. (area/tooling, kind/feature, 2–3d)
3. Documentation generator from KDoc with runnable examples. (area/tooling, kind/docs, 3–4d)

Milestone 8 — Security defaults (ongoing)

1. CSP generator with nonces for inline scripts. (area/security, kind/feature, 2d)
2. HTML auto‑escape; unsafeHtml() explicit opt‑out. (area/security, kind/refactor, 1d)
3. CSRF protection on mutations (cookies SameSite strict or double submit). (area/security, kind/feature, 2d)
4. Input validation derived from Kotlin models shared to client. (area/security, kind/feature, 2–3d)

Migration and compatibility

- Provide adapters between existing Page/DynamicPage and new components. (area/dsl, kind/feature, 2–3d)
- Feature flags (Void.features.*) to gate hydration/state features. (area/tooling, kind/feature, 1d)
- Deprecation plan with warnings; keep old APIs until vNext. (area/dsl, kind/docs, 1d)

Examples and docs

1. Example projects
    - 01-basic-routing, 02-ssr-state, 03-rpc-and-hydration, 04-sdui. (area/docs, kind/feature, 3–5d)
2. Guides
    - Routing DSL, State atoms, Client runtime, RPC + codegen, Caching, Security. (area/docs, kind/docs, 4–6d)
3. Reference
    - API reference extracted from KDoc. (area/docs, kind/docs, 2–3d)

Housekeeping & CI

1. CI workflows
    - Build, Test, Lint (Kotlin/JVM + Kotlin/JS), Publish snapshots. (area/tooling, kind/feature, 2–3d)
2. Release process
    - Versioning, changelog, artifacts (Maven). (area/tooling, kind/feature, 2–3d)
3. Qodana rules tuned for DSL files and generated code. (area/tooling, kind/chore, 0.5d)

Suggested GitHub issues (titles + branch names)

- [M1] fileDownload helper — feature/file-download-helper
- [M1] Error page enrich + structured logging — feature/error-page-context
- [M1] KSP: router validation + routes.json — feature/ksp-router-validation
- [M1] Tailwind manifest generation — feature/tailwind-manifest
- [M1] Query parsing semantics (duplicate keys, empty vs missing, no decode) — docs/query-parsing-semantics
- [M2] Server atoms and store primitives — feature/server-atoms
- [M2] WebSocket state channel with snapshots/patches — feature/state-ws
- [M2] Hydration bridge (SSR snapshot embed) — feature/hydration-bridge
- [M2] Demo: Counter/Cart live updates — feature/demo-live-state
- [M3] Kotlin/JS client runtime (@void/client-kt) — feature/kotlinjs-client-runtime
- [M3] RPC annotation + KSP Kotlin/JS client stubs — feature/rpc-kotlinjs-codegen
- [M3] Dev workflow: Kotlin/JS + backend proxy — feature/dev-hmr-kotlinjs
- [M3] Gradle ergonomics: tailwindGen, assetsBundle, devServer — feature/gradle-tasks
- [M3] Tailwind pipeline rewrite + exclusions — feature/tailwind-pipeline-rewrite
- [M3] SSR + hydration demo page — feature/ssr-hydration-demo
- [M4] SDUI attributes and fragments — feature/sdui-primitives
- [M5] Route groups and content negotiation — feature/router-polish
- [M5] Static assets hashed URLs + ETag — feature/assets-cache
- [M5] Rate limiting middleware — feature/rate-limit
- [M6] SSR cache + vary strategies — feature/ssr-cache
- [M6] Data loader layer — feature/dataloader
- [M6] Streaming HTML support — feature/streaming-html
- [M7] CLI tooling and state inspector — feature/cli-inspector
- [M8] Security defaults (CSP, CSRF, validation) — feature/security-defaults
- Docs: examples and guides — docs/examples-and-guides
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

Milestone 1 — KTS experience and routing foundation

- Response helpers and content DSL
  ```kotlin
  get("/download/report") {
      val bytes = reportService.generate()
      fileDownload(bytes, filename = "report.csv")
  }

  get("/old") { redirect(seeOther("/new")) }

  get("/missing") { notFound(html(ErrorPage("Nope"))) }

  page("Users") {
      meta { description = "Users listing" }
      head {
          includeTailwind() // reads TailwindGen manifest in M1-6
          script(module = jsModule("app"))
      }
      body {
          container {
              h1("Users")
              mount(UserListComponent()) // SSR + hydrate later in M3
          }
      }
  }
  ```

- Error handling improvements (exception page context)
  ```kotlin
  class MyExceptionPage : IExceptionPage {
      override var e: Exception? = null
      override val newPage = ExceptionPageContext(
          statusCode = 500,
          statusMessage = "Internal Error",
          headers = headersOf("X-TraceId" to request.traceId)
      )

      override val page: String get() = buildHtml {
          h1 { +"Oops" }
          pre { +e?.message.orEmpty() }
          small { +"traceId: ${'$'}{request.traceId}" }
      }
  }
  ```

- Gradle ergonomics (tasks)
  ```bash
  ./gradlew tailwindGen
  ./gradlew assetsBundle
  ./gradlew devServer -Dvoid.env=dev
  ```

- TailwindGen manifest usage in page head
  ```kotlin
  head {
      includeTailwind() // resolves to /assets/tailwind-<hash>.css
  }
  ```

Milestone 2 — Server‑authoritative reactive state

- Reactive store primitives
  ```kotlin
  // Server
  val cartAtom = Store.atom(name = "cart", initial = Cart())

  suspend fun addToCart(item: Item) {
      cartAtom.set { c -> c.copy(items = c.items + item) }
  }
  ```

- WebSocket state channel (protocol shape)
  ```text
  // Client -> Server
  SUBSCRIBE { "atom": "cart", "version": 0 }
  // Server -> Client
  FULL { "atom": "cart", "version": 1, "data": { ... } }
  PATCH { "atom": "cart", "version": 2, "diff": [ ["add", "/items/2", { ... }] ] }
  ```

- Hydration bridge embed
  ```kotlin
  // Server-side page render snippet
  body {
      // ... HTML ...
      scriptRaw("""
          window.__VOID_STATE__ = ${'$'}{serialize(mapOf(
              "cart" to Snapshot(v = cartAtom.state.valueVersion, data = cartAtom.state.value)
          ))};
      """.trimIndent())
  }
  ```

- Sample feature: Counter demo
  ```kotlin
  val counter = Store.atom("counter", 0)

  post("/rpc/increment") {
      counter.set { it + 1 }
      ok(json(Unit))
  }
  ```

Milestone 3 — Client DSL: Kotlin/JS IR + RPC

- Kotlin/JS client runtime usage
  ```kotlin
  // Kotlin/JS (browser)
  val cart = atom<Cart>("cart")

  @Component
  fun CartWidget() = h("div") {
      val (c, setC) = useAtom(cart)
      button(onClick = { rpc.addToCart(AddToCart(id = 123)) }) { text("Add") }
      ul {
          for (i in c.items) li { text(i.name) }
      }
  }
  ```

- RPC annotation in Kotlin and generated client usage
  ```kotlin
  // Shared on server; KSP generates Kotlin/JS client stub `rpc.addToCart(...)`
  @Rpc("addToCart")
  suspend fun addToCart(input: AddToCart): Unit = addToCart(input.item)

  // Kotlin/JS client side call
  suspend fun onClick() {
      rpc.addToCart(AddToCart(id = 1))
  }
  ```

- Dev workflow with Kotlin/JS and backend proxy
  ```bash
  # run Kotlin/JS dev server with HMR
  ./gradlew jsBrowserDevelopmentRun
  # backend dev server proxies /assets to http://localhost:8080 (or JS dev server port)
  ./gradlew devServer -Dvoid.env=dev
  ```

- SSR + hydration demo mounting point
  ```kotlin
  body {
      div(id = "app")
      script(type = Module) { src = assetsUrl("client.js") }
  }
  ```

Milestone 4 — Server‑Driven UI primitives (SDUI)

- HTMX‑style attributes in HTML builder
  ```kotlin
  body {
      button(attrs = mapOf("hx-get" to "/fragment/user-list", "hx-swap" to "outerHTML")) {
          +"Load users"
      }
  }
  ```

- Fragment endpoint returning partial HTML
  ```kotlin
  get("/fragment/user-list") {
      ok(html(UserListFragment()))
      headers { put("HX-Trigger", "usersLoaded") }
  }
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

- Static assets with hashed URLs
  ```kotlin
  head { linkStylesheet(assetsUrl("tailwind.css")) } // resolves to hashed file via manifest
  ```

- Rate limiting middleware
  ```kotlin
  before(RateLimit::class) // token bucket default policy
  ```

Milestone 6 — Performance & caching

- SSR cache with vary strategies
  ```kotlin
  get("/home") {
      cache(key = "home:${'$'}{user.id}", ttl = 10.seconds) {
          ok(html(HomePage(user)))
      }
  }
  ```

- Data loader layer
  ```kotlin
  val userById = loader<Int, User> { ids -> repo.loadUsers(ids) }
  suspend fun profile(id: Int) = userById.load(id)
  ```

- Streaming HTML
  ```kotlin
  ok(streamingHtml {
      chunk { +"<h1>Title</h1>" }
      chunk { +render(Header()) }
      chunk { +render(SlowSection()) }
  })
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

- CSP with nonces
  ```kotlin
  head {
      csp { defaultSrc("'self'"); scriptSrc("'self'", nonce(request.cspNonce)) }
      scriptRaw("console.log('safe')", nonce = request.cspNonce)
  }
  ```

- HTML auto‑escape and explicit unsafe
  ```kotlin
  div { +userInput }          // escaped by default
  div { unsafeHtml(rawHtml) } // opt‑out
  ```

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

Migration and compatibility

- Adapters between Page/DynamicPage and components
  ```kotlin
  mount(legacyPage) // adapter renders inside component tree
  ```

- Feature flags gating hydration/state
  ```kotlin
  if (Void.features.hydration) {
      head { script(module = jsModule("app")) }
  }
  ```
