package io.void.router.util

import io.void.html.page.Page
import io.void.router.exceptions.RouteNoTargetException
import io.void.router.exceptions.RouteTargetUsedException
import java.util.concurrent.ConcurrentHashMap

internal interface RouteCheck {
    fun handleTargetChecking(
        route: Page<*>,
        routes: ConcurrentHashMap<String, Page<*>>,
    ) {
        if (routes.containsKey(route.target)) {
            throw RouteTargetUsedException(target = route.target)
        } else {
            if (route.target.startsWith("/")) {
                routes[route.target] = route
            } else {
                throw RouteNoTargetException(target = route.target)
            }
        }
    }
}
