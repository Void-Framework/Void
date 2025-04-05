package io.void.js.exception

import io.void.js.keywords.Keyword

class JsSyntaxException(keyword: Keyword): Exception("Cannot use ${keyword.jsReturn}") {
}