package io.void.js.keywords.event.exception

import io.void.js.keywords.event.Event

class FunctionNotVariableException(event: Event): Exception("The function for ${event.eventType.eventName}, isn't stored as a variable")