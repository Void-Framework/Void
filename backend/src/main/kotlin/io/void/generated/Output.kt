package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class Output(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "output") {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(null)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.Output(vararg attribute: Attribute, _children: Element.() -> Unit): Output {
        val Output = Output(
            attributes = attribute,
            function = _children
        )
        children!!.add(Output)
        return Output
    }
