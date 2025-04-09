package io.void.js.keywords.variable

import io.void.js.keywords.Keyword

interface Variable<T>: Keyword {
    val name: String
    val value: T?
}