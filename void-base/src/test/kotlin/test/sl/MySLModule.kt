package test.sl

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.ok
import io.voidx.page.route

class MySLModule : Bootstrap.Module {
    override fun onRouterCreated(ctx: Bootstrap.Context) {
        ctx.addRoute(
            route("/sl") { GET { ok("sl") } },
        )
    }
}
