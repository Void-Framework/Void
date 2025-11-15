package io.voidx.generated

import io.voidx.html.Attribute
import io.voidx.html.Element
import io.voidx.html.ElementWithChildren
import io.voidx.html.HElement
import kotlin.reflect.KClass

class H5(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "h5"),
    HElement {
    override val acceptedChildren: MutableList<KClass<out Element>?> =
        mutableListOf(
            A::class,
            Abbr::class,
            B::class,
            Bdi::class,
            Bdo::class,
            Br::class,
            Cite::class,
            Code::class,
            Data::class,
            Dfn::class,
            Em::class,
            I::class,
            Img::class,
            Kbd::class,
            Mark::class,
            Q::class,
            Ruby::class,
            S::class,
            Samp::class,
            Small::class,
            Span::class,
            Strong::class,
            Sub::class,
            Sup::class,
            Time::class,
            U::class,
            Var::class,
            Wbr::class,
        )

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.H5(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): H5 {
    val H5 =
        H5(
            attributes = attribute,
            function = _children,
        )
    children!!.add(H5)
    return H5
}
