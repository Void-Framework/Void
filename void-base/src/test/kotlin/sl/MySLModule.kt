package sl

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.ok
import io.voidx.page.route

class MySLModule : Bootstrap.Module {
    /**
     * Registers the `/sl` GET route that responds with the plain body "sl".
     *
     * @param ctx Bootstrap context used to add routes during router initialization.
     */
    override fun onRouterCreated(ctx: Bootstrap.Context) {
        ctx.addRoute(
            route("/sl") { GET { _, _ -> ok("sl") } },
        )
    }
}
