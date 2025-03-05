package main.router.exceptions

class RouteNoTargetException(target: String): Exception("$target is incorrectly formatted/doesn't being with /")