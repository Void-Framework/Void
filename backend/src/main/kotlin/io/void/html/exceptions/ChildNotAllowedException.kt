package io.void.html.exceptions

import io.void.html.Element

/** Thrown when an element receives a direct child whose type is not permitted. */
class ChildNotAllowedException(
    child: Element,
    parent: Element,
) : Exception("${parent.name} doesn't take ${child.name} as it's child element.")

/**
 * Thrown when a [io.void.html.Fractal] fragment contains a nested child whose
 * type is not permitted by the parent element.
 */
class FragmentChildNotAllowedException(
    parent: Element,
) : Exception("${parent.name} doesn't take one of the Fragment's child as it's child element.")
