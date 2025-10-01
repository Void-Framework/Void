package io.void.generated

import io.void.html.Attribute
import io.void.html.Element
import io.void.html.ElementWithChildren
import kotlin.reflect.KClass

class P(
    vararg attributes: Attribute,
    function: Element.() -> Unit,
) : ElementWithChildren(name = "p") {
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
            Input::class,
            Kbd::class,
            Label::class,
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
            Var::class,
        )

    init {
        this.apply(function)
        addAttributes(*attributes)
    }
}

fun Element.P(
    vararg attribute: Attribute,
    _children: Element.() -> Unit,
): P {
    val P =
        P(
            attributes = attribute,
            function = _children,
        )
    children!!.add(P)
    return P
}
