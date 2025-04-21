import io.void.js.keywords.Function
import io.void.js.keywords.Keyword
import io.void.js.keywords.variable.Variable

interface JsValue<T> {
    fun toJs(): String
}

// Implementation for direct values
data class DirectValue<T>(private val value: T) : JsValue<T> {
    override fun toJs(): String = when (value) {
        is String -> "\"$value\""
        is Keyword -> value.render()
        else -> value.toString()
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