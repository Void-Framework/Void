package io.voidx.util

import io.voidx.bootstrap.Bootstrap

@Deprecated(
    message = "HtmlIntegration has been replaced by Bootstrap hooks. Use Bootstrap.registerKtsHandler and Bootstrap.registerJsAndCss.",
    level = DeprecationLevel.ERROR,
)
object HtmlIntegration {
    @Deprecated("Use Bootstrap.registerKtsHandler", ReplaceWith("Bootstrap.registerKtsHandler(fn)"))
    fun registerKtsPage(fn: io.voidx.bootstrap.GetKtsPageFn) = Bootstrap.registerKtsHandler(fn)

    @Deprecated("Use Bootstrap.registerJsAndCss", ReplaceWith("Bootstrap.registerJsAndCss(fn)"))
    fun registerJsAndCss(fn: io.voidx.bootstrap.HandleJsAndCss) = Bootstrap.registerJsAndCss(fn)
}
