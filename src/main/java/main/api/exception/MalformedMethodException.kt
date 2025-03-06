package main.api.exception

class MalformedMethodException(override val message: String): Exception(message = message) {
}