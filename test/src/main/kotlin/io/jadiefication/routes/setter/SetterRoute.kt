package io.jadiefication.routes.setter

import io.void.dto.http.ResponseDTO
import io.void.html.page.jsonRoute

val setterRoute =
    jsonRoute("/setter") {
        ResponseDTO.json(
            mutableMapOf(
                "name" to "Jade",
                "age" to 20,
                "isStudent" to true,
            ),
            200,
            "All is fine",
        )
    }
