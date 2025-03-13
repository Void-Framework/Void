package io.void.html.exceptions

import io.void.html.element.Element

class ChildNotAllowedException(child: Element, parent: Element): Exception("${parent.name} doesn't take ${child.name} as it's child element.")