package main.java.main.DTO

data class RequestDTO(var method: String, var target: String, var headers: Map<String, String>, var body: String)