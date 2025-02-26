package main.java.main.DTO

data class ResponseDTO(var status: Int, var statusText: String, var headers: Map<String, String>, var body: String)