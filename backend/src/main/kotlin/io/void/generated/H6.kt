package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import io.void.html.HElement
import kotlin.reflect.KClass

class H6(vararg attributes: Attribute, function: Element.() -> Unit): ElementWithChildren(name = "h6"), HElement {
    override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(A::class, Abbr::class, B::class, Bdi::class, Bdo::class, Br::class, Cite::class, Code::class, Data::class, Dfn::class, Em::class, I::class, Img::class, Kbd::class, Mark::class, Q::class, Ruby::class, S::class, Samp::class, Small::class, Span::class, Strong::class, Sub::class, Sup::class, Time::class, U::class, Var::class, Wbr::class)
    init {
        this.apply(function)
        addAttributes(*attributes)
    }

}    fun Element.H6(vararg attribute: Attribute, _children: Element.() -> Unit): H6 {
        val H6 = H6(
            attributes = attribute,
            function = _children
        )
        children!!.add(H6)
        return H6
    }
