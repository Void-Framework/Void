package io.void.stream

import org.jetbrains.annotations.ApiStatus

@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@ApiStatus.Experimental
annotation class Streamable()
