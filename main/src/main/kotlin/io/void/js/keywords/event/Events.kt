package io.void.js.keywords.event

enum class Events {
    // Window & Document events
    LOAD,
    UNLOAD,
    RESIZE,
    SCROLL,
    HASHCHANGE,
    ERROR,
    BEFOREUNLOAD,
    PAGEHIDE,
    PAGESHOW,

    // Focus events
    FOCUS,
    BLUR,
    FOCUSIN,
    FOCUSOUT,

    // Mouse events
    CLICK,
    DBLCLICK,
    MOUSEDOWN,
    MOUSEUP,
    MOUSEMOVE,
    MOUSEOVER,
    MOUSEOUT,
    MOUSEENTER,
    MOUSELEAVE,
    WHEEL,

    // Keyboard events
    KEYDOWN,
    KEYPRESS, // Deprecated
    KEYUP,

    // Form & Input events
    CHANGE,
    INPUT,
    INVALID,
    SUBMIT,
    RESET,
    SEARCH,
    SELECT,

    // Clipboard events
    COPY,
    CUT,
    PASTE,

    // Drag & Drop events
    DRAG,
    DRAGSTART,
    DRAGEND,
    DRAGENTER,
    DRAGLEAVE,
    DRAGOVER,
    DROP,

    // Media events
    CANPLAY,
    CANPLAYTHROUGH,
    PLAY,
    PLAYING,
    PAUSE,
    ENDED,
    VOLUMECHANGE,
    TIMEUPDATE,
    DURATIONCHANGE,
    EMPTIED,
    STALLED,
    WAITING,

    // Animation & Transition events
    ANIMATIONSTART,
    ANIMATIONITERATION,
    ANIMATIONEND,
    TRANSITIONSTART,
    TRANSITIONRUN,
    TRANSITIONEND,
    TRANSITIONCANCEL,

    // Pointer events
    POINTERDOWN,
    POINTERUP,
    POINTERMOVE,
    POINTEROVER,
    POINTEROUT,
    POINTERENTER,
    POINTERLEAVE,
    GOTPOINTERCAPTURE,
    LOSTPOINTERCAPTURE,
    POINTERCANCEL,

    // Other events
    ABORT,
    BEFOREPRINT,
    AFTERPRINT,
    MESSAGE,
    CONTEXTMENU,
    CANCEL,
    CUECHANGE,
    AUXCLICK,
    TOUCHSTART,
    TOUCHMOVE,
    TOUCHEND,
    TOUCHCANCEL,

    // ResizeObserver-like (virtual or optional)
    RESIZE_OBSERVED,

    // FormData events
    FORMDATA,

    // Page visibility
    VISIBILITYCHANGE,

    // Selection events
    SELECTIONCHANGE,

    // Device orientation & motion
    DEVICEORIENTATION,
    DEVICEMOTION,

    // Pointer events (extra)
    POINTERRAWUPDATE,

    // Shadow DOM
    SLOTCHANGE,

    // WebGL context events
    WEBGLCONTEXTLOST,
    WEBGLCONTEXTRESTORED
}