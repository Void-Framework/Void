package io.void.js.keywords.event

import io.void.js.keywords.DirectValue
import io.void.js.keywords.JsValue

// Assuming JsEvent and DefaultEvent exist in your project structure
// interface JsEvent // Placeholder
// data class DefaultEvent(val name: String) : JsEvent // Placeholder

enum class Events {
    // Window & Document events
    LOAD {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    UNLOAD {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    RESIZE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    SCROLL {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    HASHCHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    ERROR {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    BEFOREUNLOAD {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    PAGEHIDE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    PAGESHOW {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Focus events
    FOCUS {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    BLUR {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    FOCUSIN {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    FOCUSOUT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Mouse events
    CLICK {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DBLCLICK {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSEDOWN {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSEUP {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSEMOVE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSEOVER {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSEOUT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSEENTER {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MOUSELEAVE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    WHEEL {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Keyboard events
    KEYDOWN {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    KEYPRESS { // Deprecated
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    KEYUP {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Form & Input events
    CHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    INPUT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    INVALID {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    SUBMIT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    RESET {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    SEARCH {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    SELECT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Clipboard events
    COPY {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    CUT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    PASTE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Drag & Drop events
    DRAG {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DRAGSTART {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DRAGEND {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DRAGENTER {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DRAGLEAVE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DRAGOVER {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DROP {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Media events
    CANPLAY {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    CANPLAYTHROUGH {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    PLAY {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    PLAYING {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    PAUSE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    ENDED {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    VOLUMECHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TIMEUPDATE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DURATIONCHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    EMPTIED {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    STALLED {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    WAITING {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Animation & Transition events
    ANIMATIONSTART {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    ANIMATIONITERATION {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    ANIMATIONEND {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TRANSITIONSTART {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TRANSITIONRUN {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TRANSITIONEND {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TRANSITIONCANCEL {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Pointer events
    POINTERDOWN {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTERUP {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTERMOVE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTEROVER {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTEROUT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTERENTER {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTERLEAVE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    GOTPOINTERCAPTURE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    LOSTPOINTERCAPTURE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    POINTERCANCEL {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Other events
    ABORT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    BEFOREPRINT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    AFTERPRINT {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    MESSAGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    CONTEXTMENU {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    CANCEL {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    CUECHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    AUXCLICK {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TOUCHSTART {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TOUCHMOVE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TOUCHEND {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    TOUCHCANCEL {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // ResizeObserver-like (virtual or optional)
    RESIZE_OBSERVED {
        override fun asJsValue(): JsValue<JsEvent> {
            // Note: This isn't a standard DOM event name.
            // Adjust the DefaultEvent value if needed for your specific implementation.
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // FormData events
    FORMDATA {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Page visibility
    VISIBILITYCHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Selection events
    SELECTIONCHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Device orientation & motion
    DEVICEORIENTATION {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    DEVICEMOTION {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Pointer events (extra)
    POINTERRAWUPDATE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Shadow DOM
    SLOTCHANGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // WebGL context events
    WEBGLCONTEXTLOST {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },
    WEBGLCONTEXTRESTORED {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    },

    // Storage
    STORAGE {
        override fun asJsValue(): JsValue<JsEvent> {
            return DirectValue<JsEvent>(DefaultEvent(this.name.lowercase()))
        }
    };

    abstract fun asJsValue(): JsValue<JsEvent>
}