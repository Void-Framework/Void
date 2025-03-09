package main.router.exceptions

class RouteTargetUsedException(target: String): Exception("$target is already used in a different route")