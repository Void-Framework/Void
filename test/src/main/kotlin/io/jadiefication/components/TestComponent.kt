package io.jadiefication.components

import io.voidx.generated.*
import io.voidx.html.Element
import io.voidx.html.Fractal
import io.voidx.html.loop

fun Element.testComponent(): Fractal {
    val testComponent =
        Fractal {
            Div("class" to "section") {
                H2 {
                    A(
                        "href" to "https://example.com",
                        "target" to "_blank",
                        "rel" to "noopener",
                    ) {
                        Fractal(_text = "Click Me Title")
                    }
                }

                Br()

                H3 {
                    Fractal("Subtitle Here")
                }

                listOf("test", "test").forEach {
                    H1 { Fractal(it) }
                    Br()
                }

                loop(0..3) {
                    H1 { Fractal("HELP $it") }
                    Br()
                }
            }
        }
    children!!.add(testComponent)
    return testComponent
}
