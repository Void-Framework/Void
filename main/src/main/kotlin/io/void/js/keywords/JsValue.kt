import io.void.html.Element
import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.string.TemplateString
import io.void.js.keywords.variable.Variable

interface JsValue<T> {
    fun toJs(): String
}

// Implementation for direct values
data class DirectValue<T>(private val value: T) : JsValue<T> {
    override fun toJs(): String = when (value) {
        is String -> if (TemplateString.isTemplateString(value)) {
            TemplateString.turnToTemplateString(value)
        } else {
            "\"$value\""
        }
        is Keyword -> value.render()
        is Element -> value.render()
        is Iterable<*> -> value.joinToString(",") { DirectValue(it).toJs() }
        is Number, Boolean -> value.toString()
        else -> "\"${value.toString()}\""
    }

    override fun toString(): String {
        return toJs()
    }
}

// Implementation for variables
data class VariableValue<T>(private val variable: Variable<T>) : JsValue<T> {
    override fun toJs(): String = variable.name
}

data class FunctionValue(private val function: Function, private val argsList: List<String> = emptyList()) : JsValue<Any?> {
    override fun toJs(): String = function.run(argsList)
}

// Extension functions to create JsValues
fun <T> T.asJsValue(): JsValue<T> = DirectValue(this)
fun <T> Variable<T>.asJsValue(): JsValue<T> = VariableValue(this)
fun Function.asJsValue(): JsValue<Any?> = FunctionValue(this)