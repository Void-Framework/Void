package main.java.main.DTO

import main.api.method.Method

data class RequestDTO(var method: Method, var target: String, var headers: Map<String, String>, var body: String)