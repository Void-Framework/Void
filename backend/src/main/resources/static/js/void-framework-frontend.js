(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', './kotlin-kotlin-stdlib.js'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('./kotlin-kotlin-stdlib.js'));
  else {
    if (typeof globalThis['kotlin-kotlin-stdlib'] === 'undefined') {
      throw new Error("Error loading module 'io.jadiefication:frontend'. Its dependency 'kotlin-kotlin-stdlib' was not found. Please, check whether 'kotlin-kotlin-stdlib' is loaded prior to 'io.jadiefication:frontend'.");
    }
    globalThis['io.jadiefication:frontend'] = factory(typeof globalThis['io.jadiefication:frontend'] === 'undefined' ? {} : globalThis['io.jadiefication:frontend'], globalThis['kotlin-kotlin-stdlib']);
  }
}(function (_, kotlin_kotlin) {
  'use strict';
  //region block: imports
  var until = kotlin_kotlin.$_$.m;
  var collectionSizeOrDefault = kotlin_kotlin.$_$.g;
  var ArrayList_init_$Create$ = kotlin_kotlin.$_$.b;
  var ensureNotNull = kotlin_kotlin.$_$.n;
  var Unit_instance = kotlin_kotlin.$_$.d;
  var ArrayList_init_$Create$_0 = kotlin_kotlin.$_$.c;
  var firstOrNull = kotlin_kotlin.$_$.i;
  var protoOf = kotlin_kotlin.$_$.l;
  var VOID = kotlin_kotlin.$_$.a;
  var asList = kotlin_kotlin.$_$.o;
  var addAll = kotlin_kotlin.$_$.e;
  var equals = kotlin_kotlin.$_$.j;
  var emptyList = kotlin_kotlin.$_$.h;
  var checkIndexOverflow = kotlin_kotlin.$_$.f;
  var initMetadataForClass = kotlin_kotlin.$_$.k;
  //endregion
  //region block: pre-declaration
  initMetadataForClass(DOMWrapper, 'DOMWrapper');
  //endregion
  function $(element) {
    var nodeList = document.querySelectorAll(element);
    // Inline function 'kotlin.collections.map' call
    var this_0 = until(0, nodeList.length);
    // Inline function 'kotlin.collections.mapTo' call
    var destination = ArrayList_init_$Create$(collectionSizeOrDefault(this_0, 10));
    var inductionVariable = this_0.c3_1;
    var last = this_0.d3_1;
    if (inductionVariable <= last)
      do {
        var item = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        var it = item;
        var tmp$ret$0 = ensureNotNull(nodeList.item(it));
        destination.g1(tmp$ret$0);
      }
       while (!(item === last));
    // Inline function 'kotlin.collections.mapNotNull' call
    // Inline function 'kotlin.collections.mapNotNullTo' call
    var destination_0 = ArrayList_init_$Create$_0();
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = destination.b();
    while (_iterator__ex2g4s.c()) {
      var element_0 = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element_0 instanceof Element ? element_0 : null;
      if (tmp0_safe_receiver == null)
        null;
      else {
        // Inline function 'kotlin.let' call
        destination_0.g1(tmp0_safe_receiver);
      }
    }
    var elements = destination_0;
    return new DOMWrapper(elements);
  }
  function DOMWrapper$one$lambda$lambda(it) {
    return Unit_instance;
  }
  function DOMWrapper$one$lambda($handler, $el, $event) {
    return function (it) {
      $handler(it);
      $el.removeEventListener($event, DOMWrapper$one$lambda$lambda);
      return Unit_instance;
    };
  }
  function DOMWrapper(elements) {
    this.k3_1 = elements;
    var tmp = this;
    var tmp_0 = firstOrNull(this.k3_1);
    var tmp0_safe_receiver = tmp_0 instanceof HTMLElement ? tmp_0 : null;
    tmp.textReturn = tmp0_safe_receiver == null ? null : tmp0_safe_receiver.innerText;
    var tmp_1 = this;
    var tmp_2 = firstOrNull(this.k3_1);
    var tmp0_safe_receiver_0 = tmp_2 instanceof HTMLElement ? tmp_2 : null;
    tmp_1.htmlReturn = tmp0_safe_receiver_0 == null ? null : tmp0_safe_receiver_0.innerHTML;
    var tmp_3 = this;
    var element = firstOrNull(this.k3_1);
    var tmp_4;
    if (element instanceof HTMLInputElement) {
      tmp_4 = element.value;
    } else {
      if (element instanceof HTMLTextAreaElement) {
        tmp_4 = element.value;
      } else {
        if (element instanceof HTMLSelectElement) {
          tmp_4 = element.value;
        } else {
          tmp_4 = null;
        }
      }
    }
    tmp_3.valReturn = tmp_4;
    var tmp_5 = this;
    var tmp0_safe_receiver_1 = firstOrNull(this.k3_1);
    tmp_5.parent = tmp0_safe_receiver_1 == null ? null : tmp0_safe_receiver_1.parentElement;
    var tmp_6 = this;
    var tmp0_safe_receiver_2 = firstOrNull(this.k3_1);
    tmp_6.children = tmp0_safe_receiver_2 == null ? null : tmp0_safe_receiver_2.children;
    var tmp_7 = this;
    var tmp0_safe_receiver_3 = firstOrNull(this.k3_1);
    var tmp1_safe_receiver = tmp0_safe_receiver_3 == null ? null : tmp0_safe_receiver_3.parentElement;
    tmp_7.siblings = tmp1_safe_receiver == null ? null : tmp1_safe_receiver.children;
  }
  protoOf(DOMWrapper).text = function (text) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element instanceof HTMLElement ? element : null;
      if (tmp0_safe_receiver == null)
        null;
      else {
        tmp0_safe_receiver.innerText = text;
      }
    }
    return this;
  };
  protoOf(DOMWrapper).l3 = function () {
    return this.textReturn;
  };
  protoOf(DOMWrapper).html = function (element) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element_0 = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element_0 instanceof HTMLElement ? element_0 : null;
      if (tmp0_safe_receiver == null)
        null;
      else {
        tmp0_safe_receiver.innerHTML = element.innerHTML;
      }
    }
    return this;
  };
  protoOf(DOMWrapper).m3 = function () {
    return this.htmlReturn;
  };
  protoOf(DOMWrapper).value = function (v) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      if (element instanceof HTMLInputElement) {
        element.value = v;
      } else {
        if (element instanceof HTMLTextAreaElement) {
          element.value = v;
        } else {
          if (element instanceof HTMLSelectElement) {
            element.value = v;
          }
        }
      }
    }
    return this;
  };
  protoOf(DOMWrapper).n3 = function (_set____db54di) {
    this.valReturn = _set____db54di;
  };
  protoOf(DOMWrapper).o3 = function () {
    return this.valReturn;
  };
  protoOf(DOMWrapper).attr = function (name) {
    var tmp0_safe_receiver = firstOrNull(this.k3_1);
    return tmp0_safe_receiver == null ? null : tmp0_safe_receiver.getAttribute(name);
  };
  protoOf(DOMWrapper).setAttr = function (name, value) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.setAttribute(name, value);
    }
  };
  protoOf(DOMWrapper).removeAttr = function (name) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.removeAttribute(name);
    }
  };
  protoOf(DOMWrapper).classes = function () {
    var tmp0_safe_receiver = firstOrNull(this.k3_1);
    return tmp0_safe_receiver == null ? null : tmp0_safe_receiver.classList;
  };
  protoOf(DOMWrapper).setProperty = function (property, value) {
    var tmp0_safe_receiver = this.style();
    if (tmp0_safe_receiver == null)
      null;
    else {
      tmp0_safe_receiver.setProperty(property, value);
    }
  };
  protoOf(DOMWrapper).style = function () {
    var tmp = firstOrNull(this.k3_1);
    var tmp0_safe_receiver = tmp instanceof HTMLElement ? tmp : null;
    return tmp0_safe_receiver == null ? null : tmp0_safe_receiver.style;
  };
  protoOf(DOMWrapper).getProperty = function (property) {
    var tmp0_safe_receiver = this.style();
    return tmp0_safe_receiver == null ? null : tmp0_safe_receiver.getPropertyValue(property);
  };
  protoOf(DOMWrapper).on = function (event, lambda) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.addEventListener(event, lambda);
    }
  };
  protoOf(DOMWrapper).p3 = function (event, lambda) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.removeEventListener(event, lambda);
    }
  };
  protoOf(DOMWrapper).off = function (event, lambda, $super) {
    lambda = lambda === VOID ? null : lambda;
    var tmp;
    if ($super === VOID) {
      this.p3(event, lambda);
      tmp = Unit_instance;
    } else {
      tmp = $super.p3.call(this, event, lambda);
    }
    return tmp;
  };
  protoOf(DOMWrapper).q3 = function () {
    return this.parent;
  };
  protoOf(DOMWrapper).r3 = function () {
    return this.children;
  };
  protoOf(DOMWrapper).s3 = function () {
    return this.siblings;
  };
  protoOf(DOMWrapper).remove = function () {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.remove();
    }
  };
  protoOf(DOMWrapper).empty = function () {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.innerHTML = '';
    }
  };
  protoOf(DOMWrapper).hide = function () {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = this.style();
      if (tmp0_safe_receiver == null)
        null;
      else {
        tmp0_safe_receiver.display = 'none';
      }
    }
  };
  protoOf(DOMWrapper).show = function () {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = this.style();
      if (tmp0_safe_receiver == null)
        null;
      else {
        tmp0_safe_receiver.display = '';
      }
    }
  };
  protoOf(DOMWrapper).find = function (selector) {
    // Inline function 'kotlin.collections.flatMap' call
    var tmp0 = this.k3_1;
    // Inline function 'kotlin.collections.flatMapTo' call
    var destination = ArrayList_init_$Create$_0();
    var _iterator__ex2g4s = tmp0.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      // Inline function 'kotlin.collections.mapNotNull' call
      var tmp0_0 = asList(element.querySelectorAll(selector));
      // Inline function 'kotlin.collections.mapNotNullTo' call
      var destination_0 = ArrayList_init_$Create$_0();
      // Inline function 'kotlin.collections.forEach' call
      var _iterator__ex2g4s_0 = tmp0_0.b();
      while (_iterator__ex2g4s_0.c()) {
        var element_0 = _iterator__ex2g4s_0.d();
        var tmp0_safe_receiver = element_0 instanceof Element ? element_0 : null;
        if (tmp0_safe_receiver == null)
          null;
        else {
          // Inline function 'kotlin.let' call
          destination_0.g1(tmp0_safe_receiver);
        }
      }
      var list = destination_0;
      addAll(destination, list);
    }
    return new DOMWrapper(destination);
  };
  protoOf(DOMWrapper).closest = function (selector) {
    // Inline function 'kotlin.collections.mapNotNull' call
    var tmp0 = this.k3_1;
    // Inline function 'kotlin.collections.mapNotNullTo' call
    var destination = ArrayList_init_$Create$_0();
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = tmp0.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element.closest(selector);
      if (tmp0_safe_receiver == null)
        null;
      else {
        // Inline function 'kotlin.let' call
        destination.g1(tmp0_safe_receiver);
      }
    }
    return new DOMWrapper(destination);
  };
  protoOf(DOMWrapper).t3 = function (selector) {
    // Inline function 'kotlin.collections.flatMap' call
    var tmp0 = this.k3_1;
    // Inline function 'kotlin.collections.flatMapTo' call
    var destination = ArrayList_init_$Create$_0();
    var _iterator__ex2g4s = tmp0.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var list = asList(element.children);
      var tmp;
      if (!(selector == null)) {
        // Inline function 'kotlin.collections.filter' call
        // Inline function 'kotlin.collections.filterTo' call
        var destination_0 = ArrayList_init_$Create$_0();
        var _iterator__ex2g4s_0 = list.b();
        while (_iterator__ex2g4s_0.c()) {
          var element_0 = _iterator__ex2g4s_0.d();
          if (element_0.matches(selector)) {
            destination_0.g1(element_0);
          }
        }
        tmp = destination_0;
      } else {
        tmp = list;
      }
      var list_0 = tmp;
      addAll(destination, list_0);
    }
    return new DOMWrapper(destination);
  };
  protoOf(DOMWrapper).childByName = function (selector, $super) {
    selector = selector === VOID ? null : selector;
    return $super === VOID ? this.t3(selector) : $super.t3.call(this, selector);
  };
  protoOf(DOMWrapper).u3 = function (selector) {
    // Inline function 'kotlin.collections.flatMap' call
    var tmp0 = this.k3_1;
    // Inline function 'kotlin.collections.flatMapTo' call
    var destination = ArrayList_init_$Create$_0();
    var _iterator__ex2g4s = tmp0.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element.parentElement;
      var tmp1_safe_receiver = tmp0_safe_receiver == null ? null : tmp0_safe_receiver.children;
      var tmp2_safe_receiver = tmp1_safe_receiver == null ? null : asList(tmp1_safe_receiver);
      var tmp;
      if (tmp2_safe_receiver == null) {
        tmp = null;
      } else {
        // Inline function 'kotlin.collections.filter' call
        // Inline function 'kotlin.collections.filterTo' call
        var destination_0 = ArrayList_init_$Create$_0();
        var _iterator__ex2g4s_0 = tmp2_safe_receiver.b();
        while (_iterator__ex2g4s_0.c()) {
          var element_0 = _iterator__ex2g4s_0.d();
          if (!equals(element_0, element)) {
            destination_0.g1(element_0);
          }
        }
        tmp = destination_0;
      }
      var tmp3_safe_receiver = tmp;
      var tmp_0;
      if (tmp3_safe_receiver == null) {
        tmp_0 = null;
      } else {
        // Inline function 'kotlin.let' call
        var tmp_1;
        if (!(selector == null)) {
          // Inline function 'kotlin.collections.filter' call
          // Inline function 'kotlin.collections.filterTo' call
          var destination_1 = ArrayList_init_$Create$_0();
          var _iterator__ex2g4s_1 = tmp3_safe_receiver.b();
          while (_iterator__ex2g4s_1.c()) {
            var element_1 = _iterator__ex2g4s_1.d();
            if (element_1.matches(selector)) {
              destination_1.g1(element_1);
            }
          }
          tmp_1 = destination_1;
        } else {
          tmp_1 = tmp3_safe_receiver;
        }
        tmp_0 = tmp_1;
      }
      var tmp4_elvis_lhs = tmp_0;
      var list = tmp4_elvis_lhs == null ? emptyList() : tmp4_elvis_lhs;
      addAll(destination, list);
    }
    return new DOMWrapper(destination);
  };
  protoOf(DOMWrapper).siblingByName = function (selector, $super) {
    selector = selector === VOID ? null : selector;
    return $super === VOID ? this.u3(selector) : $super.u3.call(this, selector);
  };
  protoOf(DOMWrapper).next = function () {
    // Inline function 'kotlin.collections.mapNotNull' call
    var tmp0 = this.k3_1;
    // Inline function 'kotlin.collections.mapNotNullTo' call
    var destination = ArrayList_init_$Create$_0();
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = tmp0.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element.nextElementSibling;
      if (tmp0_safe_receiver == null)
        null;
      else {
        // Inline function 'kotlin.let' call
        destination.g1(tmp0_safe_receiver);
      }
    }
    return new DOMWrapper(destination);
  };
  protoOf(DOMWrapper).prev = function () {
    // Inline function 'kotlin.collections.mapNotNull' call
    var tmp0 = this.k3_1;
    // Inline function 'kotlin.collections.mapNotNullTo' call
    var destination = ArrayList_init_$Create$_0();
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = tmp0.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element.previousElementSibling;
      if (tmp0_safe_receiver == null)
        null;
      else {
        // Inline function 'kotlin.let' call
        destination.g1(tmp0_safe_receiver);
      }
    }
    return new DOMWrapper(destination);
  };
  protoOf(DOMWrapper).append = function (element) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element_0 = _iterator__ex2g4s.d();
      element_0.appendChild(element.cloneNode(true));
    }
    return this;
  };
  protoOf(DOMWrapper).prepend = function (element) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element_0 = _iterator__ex2g4s.d();
      element_0.insertBefore(element.cloneNode(true), element_0.firstChild);
    }
    return this;
  };
  protoOf(DOMWrapper).before = function (element) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element_0 = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element_0.parentElement;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.insertBefore(element.cloneNode(true), element_0);
    }
    return this;
  };
  protoOf(DOMWrapper).after = function (element) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element_0 = _iterator__ex2g4s.d();
      var tmp0_safe_receiver = element_0.parentElement;
      if (tmp0_safe_receiver == null)
        null;
      else
        tmp0_safe_receiver.insertBefore(element.cloneNode(true), element_0.nextSibling);
    }
    return this;
  };
  protoOf(DOMWrapper).addClass = function (name) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.classList.add(name);
    }
  };
  protoOf(DOMWrapper).removeClass = function (name) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.classList.remove(name);
    }
  };
  protoOf(DOMWrapper).toggleClass = function (name) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      element.classList.toggle(name);
    }
  };
  protoOf(DOMWrapper).one = function (event, handler) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var wrapper = DOMWrapper$one$lambda(handler, element, event);
      element.addEventListener(event, wrapper);
    }
  };
  protoOf(DOMWrapper).trigger = function (event) {
    // Inline function 'kotlin.collections.forEach' call
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      var ev = new Event(event);
      element.dispatchEvent(ev);
    }
  };
  protoOf(DOMWrapper).each = function (lambda) {
    // Inline function 'kotlin.collections.forEachIndexed' call
    var index = 0;
    var _iterator__ex2g4s = this.k3_1.b();
    while (_iterator__ex2g4s.c()) {
      var item = _iterator__ex2g4s.d();
      var _unary__edvuaz = index;
      index = _unary__edvuaz + 1 | 0;
      lambda(item, checkIndexOverflow(_unary__edvuaz));
    }
  };
  //region block: exports
  function $jsExportAll$(_) {
    var $io = _.io || (_.io = {});
    var $io$void = $io.void || ($io.void = {});
    var $io$void$jquery = $io$void.jquery || ($io$void.jquery = {});
    $io$void$jquery.$ = $;
    $io$void$jquery.DOMWrapper = DOMWrapper;
  }
  $jsExportAll$(_);
  kotlin_kotlin.$jsExportAll$(_);
  //endregion
  return _;
}));

//# sourceMappingURL=void-framework-frontend.js.map
