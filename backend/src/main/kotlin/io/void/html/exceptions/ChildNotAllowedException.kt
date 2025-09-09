package io.void.html.exceptions

import io.void.html.Element

class ChildNotAllowedException(child: Element, parent: Element): Exception("${parent.name} doesn't take ${child.name} as it's child element.")
class FragmentChildNotAllowedException(parent: Element): Exception("${parent.name} doesn't take one of the Fragment's child as it's child element.")