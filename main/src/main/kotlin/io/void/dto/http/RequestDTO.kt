package io.void.dto.http

import io.void.api.method.Method

data class RequestDTO(var method: Method, var target: String, var headers: Map<String, String>, var body: String)