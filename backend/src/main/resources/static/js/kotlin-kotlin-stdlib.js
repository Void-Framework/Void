//region block: polyfills
(function () {
  if (typeof globalThis === 'object')
    return;
  Object.defineProperty(Object.prototype, '__magic__', {get: function () {
    return this;
  }, configurable: true});
  __magic__.globalThis = __magic__;
  delete Object.prototype.__magic__;
}());
if (typeof Math.imul === 'undefined') {
  Math.imul = function imul(a, b) {
    return (a & 4.29490176E9) * (b & 65535) + (a & 65535) * (b | 0) | 0;
  };
}
if (typeof ArrayBuffer.isView === 'undefined') {
  ArrayBuffer.isView = function (a) {
    return a != null && a.__proto__ != null && a.__proto__.__proto__ === Int8Array.prototype.__proto__;
  };
}
//endregion
(function (factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports'], factory);
  else if (typeof exports === 'object')
    factory(module.exports);
  else
    globalThis['kotlin-kotlin-stdlib'] = factory(typeof globalThis['kotlin-kotlin-stdlib'] === 'undefined' ? {} : globalThis['kotlin-kotlin-stdlib']);
}(function (_) {
  'use strict';
  //region block: imports
  var imul = Math.imul;
  var isView = ArrayBuffer.isView;
  //endregion
  //region block: pre-declaration
  initMetadataForInterface(CharSequence, 'CharSequence');
  initMetadataForClass(Number_0, 'Number');
  initMetadataForClass(Char, 'Char');
  initMetadataForCompanion(Companion);
  initMetadataForInterface(Collection, 'Collection');
  function asJsReadonlyArrayView() {
    return createJsReadonlyArrayViewFrom(this);
  }
  initMetadataForInterface(KtList, 'List', VOID, VOID, [Collection]);
  initMetadataForCompanion(Companion_0);
  initMetadataForClass(Long, 'Long', VOID, Number_0);
  initMetadataForClass(JsArrayView, 'JsArrayView', JsArrayView, Array);
  initMetadataForClass(AbstractCollection, 'AbstractCollection', VOID, VOID, [Collection]);
  initMetadataForClass(AbstractList, 'AbstractList', VOID, AbstractCollection, [AbstractCollection, KtList]);
  initMetadataForClass(asList$1, VOID, VOID, AbstractList);
  initMetadataForObject(Unit, 'Unit');
  initMetadataForClass(AbstractMutableCollection, 'AbstractMutableCollection', VOID, AbstractCollection, [AbstractCollection, Collection]);
  initMetadataForClass(IteratorImpl, 'IteratorImpl');
  initMetadataForClass(ListIteratorImpl, 'ListIteratorImpl', VOID, IteratorImpl);
  initMetadataForClass(AbstractMutableList, 'AbstractMutableList', VOID, AbstractMutableCollection, [AbstractMutableCollection, KtList, Collection]);
  initMetadataForClass(SubList, 'SubList', VOID, AbstractMutableList);
  initMetadataForCompanion(Companion_1);
  initMetadataForClass(ArrayList, 'ArrayList', ArrayList_init_$Create$, AbstractMutableList, [AbstractMutableList, KtList, Collection]);
  initMetadataForClass(Exception, 'Exception', Exception_init_$Create$, Error);
  initMetadataForClass(RuntimeException, 'RuntimeException', RuntimeException_init_$Create$, Exception);
  initMetadataForClass(IllegalArgumentException, 'IllegalArgumentException', IllegalArgumentException_init_$Create$, RuntimeException);
  initMetadataForClass(IllegalStateException, 'IllegalStateException', IllegalStateException_init_$Create$, RuntimeException);
  initMetadataForClass(UnsupportedOperationException, 'UnsupportedOperationException', UnsupportedOperationException_init_$Create$, RuntimeException);
  initMetadataForClass(NoSuchElementException, 'NoSuchElementException', NoSuchElementException_init_$Create$, RuntimeException);
  initMetadataForClass(IndexOutOfBoundsException, 'IndexOutOfBoundsException', IndexOutOfBoundsException_init_$Create$, RuntimeException);
  initMetadataForClass(ArithmeticException, 'ArithmeticException', ArithmeticException_init_$Create$, RuntimeException);
  initMetadataForClass(NullPointerException, 'NullPointerException', NullPointerException_init_$Create$, RuntimeException);
  initMetadataForClass(ClassCastException, 'ClassCastException', ClassCastException_init_$Create$, RuntimeException);
  initMetadataForClass(StringBuilder, 'StringBuilder', StringBuilder_init_$Create$, VOID, [CharSequence]);
  initMetadataForClass(SubList_0, 'SubList', VOID, AbstractList);
  initMetadataForClass(IteratorImpl_0, 'IteratorImpl');
  initMetadataForClass(ListIteratorImpl_0, 'ListIteratorImpl', VOID, IteratorImpl_0);
  initMetadataForCompanion(Companion_2);
  initMetadataForObject(EmptyList, 'EmptyList', VOID, VOID, [KtList]);
  initMetadataForObject(EmptyIterator, 'EmptyIterator');
  initMetadataForClass(IntIterator, 'IntIterator');
  initMetadataForCompanion(Companion_3);
  initMetadataForClass(IntProgression, 'IntProgression');
  initMetadataForClass(IntRange, 'IntRange', VOID, IntProgression);
  initMetadataForClass(IntProgressionIterator, 'IntProgressionIterator', VOID, IntIterator);
  initMetadataForCompanion(Companion_4);
  //endregion
  function CharSequence() {
  }
  function Number_0() {
  }
  function indexOf(_this__u8e3s4, element) {
    if (element == null) {
      var inductionVariable = 0;
      var last = _this__u8e3s4.length - 1 | 0;
      if (inductionVariable <= last)
        do {
          var index = inductionVariable;
          inductionVariable = inductionVariable + 1 | 0;
          if (_this__u8e3s4[index] == null) {
            return index;
          }
        }
         while (inductionVariable <= last);
    } else {
      var inductionVariable_0 = 0;
      var last_0 = _this__u8e3s4.length - 1 | 0;
      if (inductionVariable_0 <= last_0)
        do {
          var index_0 = inductionVariable_0;
          inductionVariable_0 = inductionVariable_0 + 1 | 0;
          if (equals(element, _this__u8e3s4[index_0])) {
            return index_0;
          }
        }
         while (inductionVariable_0 <= last_0);
    }
    return -1;
  }
  function lastIndexOf(_this__u8e3s4, element) {
    if (element == null) {
      var inductionVariable = _this__u8e3s4.length - 1 | 0;
      if (0 <= inductionVariable)
        do {
          var index = inductionVariable;
          inductionVariable = inductionVariable + -1 | 0;
          if (_this__u8e3s4[index] == null) {
            return index;
          }
        }
         while (0 <= inductionVariable);
    } else {
      var inductionVariable_0 = _this__u8e3s4.length - 1 | 0;
      if (0 <= inductionVariable_0)
        do {
          var index_0 = inductionVariable_0;
          inductionVariable_0 = inductionVariable_0 + -1 | 0;
          if (equals(element, _this__u8e3s4[index_0])) {
            return index_0;
          }
        }
         while (0 <= inductionVariable_0);
    }
    return -1;
  }
  function joinToString(_this__u8e3s4, separator, prefix, postfix, limit, truncated, transform) {
    separator = separator === VOID ? ', ' : separator;
    prefix = prefix === VOID ? '' : prefix;
    postfix = postfix === VOID ? '' : postfix;
    limit = limit === VOID ? -1 : limit;
    truncated = truncated === VOID ? '...' : truncated;
    transform = transform === VOID ? null : transform;
    return joinTo(_this__u8e3s4, StringBuilder_init_$Create$(), separator, prefix, postfix, limit, truncated, transform).toString();
  }
  function joinTo(_this__u8e3s4, buffer, separator, prefix, postfix, limit, truncated, transform) {
    separator = separator === VOID ? ', ' : separator;
    prefix = prefix === VOID ? '' : prefix;
    postfix = postfix === VOID ? '' : postfix;
    limit = limit === VOID ? -1 : limit;
    truncated = truncated === VOID ? '...' : truncated;
    transform = transform === VOID ? null : transform;
    buffer.a(prefix);
    var count = 0;
    var inductionVariable = 0;
    var last = _this__u8e3s4.length;
    $l$loop: while (inductionVariable < last) {
      var element = _this__u8e3s4[inductionVariable];
      inductionVariable = inductionVariable + 1 | 0;
      count = count + 1 | 0;
      if (count > 1) {
        buffer.a(separator);
      }
      if (limit < 0 || count <= limit) {
        appendElement(buffer, element, transform);
      } else
        break $l$loop;
    }
    if (limit >= 0 && count > limit) {
      buffer.a(truncated);
    }
    buffer.a(postfix);
    return buffer;
  }
  function joinToString_0(_this__u8e3s4, separator, prefix, postfix, limit, truncated, transform) {
    separator = separator === VOID ? ', ' : separator;
    prefix = prefix === VOID ? '' : prefix;
    postfix = postfix === VOID ? '' : postfix;
    limit = limit === VOID ? -1 : limit;
    truncated = truncated === VOID ? '...' : truncated;
    transform = transform === VOID ? null : transform;
    return joinTo_0(_this__u8e3s4, StringBuilder_init_$Create$(), separator, prefix, postfix, limit, truncated, transform).toString();
  }
  function joinTo_0(_this__u8e3s4, buffer, separator, prefix, postfix, limit, truncated, transform) {
    separator = separator === VOID ? ', ' : separator;
    prefix = prefix === VOID ? '' : prefix;
    postfix = postfix === VOID ? '' : postfix;
    limit = limit === VOID ? -1 : limit;
    truncated = truncated === VOID ? '...' : truncated;
    transform = transform === VOID ? null : transform;
    buffer.a(prefix);
    var count = 0;
    var _iterator__ex2g4s = _this__u8e3s4.b();
    $l$loop: while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      count = count + 1 | 0;
      if (count > 1) {
        buffer.a(separator);
      }
      if (limit < 0 || count <= limit) {
        appendElement(buffer, element, transform);
      } else
        break $l$loop;
    }
    if (limit >= 0 && count > limit) {
      buffer.a(truncated);
    }
    buffer.a(postfix);
    return buffer;
  }
  function firstOrNull(_this__u8e3s4) {
    return _this__u8e3s4.f() ? null : _this__u8e3s4.e(0);
  }
  function until(_this__u8e3s4, to) {
    if (to <= -2147483648)
      return Companion_getInstance_3().g_1;
    return numberRangeToNumber(_this__u8e3s4, to - 1 | 0);
  }
  function _get_value__a43j40($this) {
    return $this;
  }
  function toString($this) {
    // Inline function 'kotlin.js.unsafeCast' call
    return String.fromCharCode(_get_value__a43j40($this));
  }
  function Char() {
  }
  protoOf(Companion).fromJsArray = function (array) {
    return createListFrom(array);
  };
  function Companion() {
  }
  var Companion_instance;
  function Companion_getInstance() {
    return Companion_instance;
  }
  function KtList() {
  }
  function Collection() {
  }
  function toString_0(_this__u8e3s4) {
    var tmp1_elvis_lhs = _this__u8e3s4 == null ? null : toString_1(_this__u8e3s4);
    return tmp1_elvis_lhs == null ? 'null' : tmp1_elvis_lhs;
  }
  function Companion_0() {
    Companion_instance_0 = this;
    this.p_1 = new Long(0, -2147483648);
    this.q_1 = new Long(-1, 2147483647);
    this.r_1 = 8;
    this.s_1 = 64;
  }
  var Companion_instance_0;
  function Companion_getInstance_0() {
    if (Companion_instance_0 == null)
      new Companion_0();
    return Companion_instance_0;
  }
  function Long(low, high) {
    Companion_getInstance_0();
    Number_0.call(this);
    this.t_1 = low;
    this.u_1 = high;
  }
  protoOf(Long).v = function (other) {
    return add(this, other);
  };
  protoOf(Long).w = function (other) {
    return divide(this, other);
  };
  protoOf(Long).x = function () {
    return this.y().v(new Long(1, 0));
  };
  protoOf(Long).y = function () {
    return new Long(~this.t_1, ~this.u_1);
  };
  protoOf(Long).z = function () {
    return this.t_1;
  };
  protoOf(Long).a1 = function () {
    return toNumber(this);
  };
  protoOf(Long).toString = function () {
    return toStringImpl(this, 10);
  };
  protoOf(Long).equals = function (other) {
    var tmp;
    if (other instanceof Long) {
      tmp = equalsLong(this, other);
    } else {
      tmp = false;
    }
    return tmp;
  };
  protoOf(Long).hashCode = function () {
    return hashCode_0(this);
  };
  protoOf(Long).valueOf = function () {
    return this.a1();
  };
  function implement(interfaces) {
    var maxSize = 1;
    var masks = [];
    var inductionVariable = 0;
    var last = interfaces.length;
    while (inductionVariable < last) {
      var i = interfaces[inductionVariable];
      inductionVariable = inductionVariable + 1 | 0;
      var currentSize = maxSize;
      var tmp0_elvis_lhs = i.prototype.$imask$;
      var imask = tmp0_elvis_lhs == null ? i.$imask$ : tmp0_elvis_lhs;
      if (!(imask == null)) {
        masks.push(imask);
        currentSize = imask.length;
      }
      var iid = i.$metadata$.iid;
      var tmp;
      if (iid == null) {
        tmp = null;
      } else {
        // Inline function 'kotlin.let' call
        tmp = bitMaskWith(iid);
      }
      var iidImask = tmp;
      if (!(iidImask == null)) {
        masks.push(iidImask);
        currentSize = Math.max(currentSize, iidImask.length);
      }
      if (currentSize > maxSize) {
        maxSize = currentSize;
      }
    }
    return compositeBitMask(maxSize, masks);
  }
  function bitMaskWith(activeBit) {
    var numberIndex = activeBit >> 5;
    var intArray = new Int32Array(numberIndex + 1 | 0);
    var positionInNumber = activeBit & 31;
    var numberWithSettledBit = 1 << positionInNumber;
    intArray[numberIndex] = intArray[numberIndex] | numberWithSettledBit;
    return intArray;
  }
  function compositeBitMask(capacity, masks) {
    var tmp = 0;
    var tmp_0 = new Int32Array(capacity);
    while (tmp < capacity) {
      var tmp_1 = tmp;
      var result = 0;
      var inductionVariable = 0;
      var last = masks.length;
      while (inductionVariable < last) {
        var mask = masks[inductionVariable];
        inductionVariable = inductionVariable + 1 | 0;
        if (tmp_1 < mask.length) {
          result = result | mask[tmp_1];
        }
      }
      tmp_0[tmp_1] = result;
      tmp = tmp + 1 | 0;
    }
    return tmp_0;
  }
  function isBitSet(_this__u8e3s4, possibleActiveBit) {
    var numberIndex = possibleActiveBit >> 5;
    if (numberIndex > _this__u8e3s4.length)
      return false;
    var positionInNumber = possibleActiveBit & 31;
    var numberWithSettledBit = 1 << positionInNumber;
    return !((_this__u8e3s4[numberIndex] & numberWithSettledBit) === 0);
  }
  function get_buf() {
    _init_properties_bitUtils_kt__nfcg4k();
    return buf;
  }
  var buf;
  function get_bufFloat64() {
    _init_properties_bitUtils_kt__nfcg4k();
    return bufFloat64;
  }
  var bufFloat64;
  var bufFloat32;
  function get_bufInt32() {
    _init_properties_bitUtils_kt__nfcg4k();
    return bufInt32;
  }
  var bufInt32;
  function get_lowIndex() {
    _init_properties_bitUtils_kt__nfcg4k();
    return lowIndex;
  }
  var lowIndex;
  function get_highIndex() {
    _init_properties_bitUtils_kt__nfcg4k();
    return highIndex;
  }
  var highIndex;
  function getNumberHashCode(obj) {
    _init_properties_bitUtils_kt__nfcg4k();
    // Inline function 'kotlin.js.jsBitwiseOr' call
    // Inline function 'kotlin.js.unsafeCast' call
    // Inline function 'kotlin.js.asDynamic' call
    if ((obj | 0) === obj) {
      return numberToInt(obj);
    }
    get_bufFloat64()[0] = obj;
    return imul(get_bufInt32()[get_highIndex()], 31) + get_bufInt32()[get_lowIndex()] | 0;
  }
  var properties_initialized_bitUtils_kt_i2bo3e;
  function _init_properties_bitUtils_kt__nfcg4k() {
    if (!properties_initialized_bitUtils_kt_i2bo3e) {
      properties_initialized_bitUtils_kt_i2bo3e = true;
      buf = new ArrayBuffer(8);
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      bufFloat64 = new Float64Array(get_buf());
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      bufFloat32 = new Float32Array(get_buf());
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      bufInt32 = new Int32Array(get_buf());
      // Inline function 'kotlin.run' call
      get_bufFloat64()[0] = -1.0;
      lowIndex = !(get_bufInt32()[0] === 0) ? 1 : 0;
      highIndex = 1 - get_lowIndex() | 0;
    }
  }
  function arrayToString(array) {
    return joinToString(array, ', ', '[', ']', VOID, VOID, arrayToString$lambda);
  }
  function arrayToString$lambda(it) {
    return toString_1(it);
  }
  function createJsReadonlyArrayViewFrom(list) {
    var tmp = createJsReadonlyArrayViewFrom$lambda(list);
    var tmp_0 = createJsReadonlyArrayViewFrom$lambda_0(list);
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_1 = UNSUPPORTED_OPERATION$ref();
    // Inline function 'kotlin.js.asDynamic' call
    var tmp_2 = UNSUPPORTED_OPERATION$ref_0();
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$2 = UNSUPPORTED_OPERATION$ref_1();
    return createJsArrayViewWith(tmp, tmp_0, tmp_1, tmp_2, tmp$ret$2);
  }
  function createJsArrayViewWith(listSize, listGet, listSet, listDecreaseSize, listIncreaseSize) {
    var arrayView = new Array();
    var tmp = Object;
    // Inline function 'kotlin.js.asDynamic' call
    var tmp$ret$0 = JsArrayView;
    tmp.setPrototypeOf(arrayView, tmp$ret$0.prototype);
    return new Proxy(arrayView, {get: function (target, prop, receiver) {
      if (prop === 'length')
        return listSize();
      var type = typeof prop;
      var index = type === 'string' || type === 'number' ? +prop : undefined;
      if (!isNaN(index))
        return listGet(index);
      return target[prop];
    }, has: function (target, key) {
      return !isNaN(key) && key < listSize();
    }, set: function (obj, prop, value) {
      if (prop === 'length') {
        var size = listSize();
        var newSize = type === 'string' || type === 'number' ? +prop : undefined;
        if (isNaN(newSize))
          throw new RangeError('invalid array length');
        if (newSize < size)
          listDecreaseSize(size - newSize);
        else
          listIncreaseSize(newSize - size);
        return true;
      }
      var type = typeof prop;
      var index = type === 'string' || type === 'number' ? +prop : undefined;
      if (isNaN(index))
        return false;
      listSet(index, value);
      return true;
    }});
  }
  function UNSUPPORTED_OPERATION() {
    throw UnsupportedOperationException_init_$Create$();
  }
  function JsArrayView() {
    Array.call(this);
  }
  function createListFrom(array) {
    // Inline function 'kotlin.js.asDynamic' call
    // Inline function 'kotlin.js.unsafeCast' call
    var tmp$ret$1 = array.slice();
    return (new ArrayList(tmp$ret$1)).e1();
  }
  function createJsReadonlyArrayViewFrom$lambda($list) {
    return function () {
      return $list.h();
    };
  }
  function createJsReadonlyArrayViewFrom$lambda_0($list) {
    return function (i) {
      return $list.e(i);
    };
  }
  function UNSUPPORTED_OPERATION$ref() {
    var l = function () {
      UNSUPPORTED_OPERATION();
      return Unit_instance;
    };
    l.callableName = 'UNSUPPORTED_OPERATION';
    return l;
  }
  function UNSUPPORTED_OPERATION$ref_0() {
    var l = function () {
      UNSUPPORTED_OPERATION();
      return Unit_instance;
    };
    l.callableName = 'UNSUPPORTED_OPERATION';
    return l;
  }
  function UNSUPPORTED_OPERATION$ref_1() {
    var l = function () {
      UNSUPPORTED_OPERATION();
      return Unit_instance;
    };
    l.callableName = 'UNSUPPORTED_OPERATION';
    return l;
  }
  function getObjectHashCode(obj) {
    // Inline function 'kotlin.js.jsIn' call
    if (!('kotlinHashCodeValue$' in obj)) {
      var hash = calculateRandomHash();
      var descriptor = new Object();
      descriptor.value = hash;
      descriptor.enumerable = false;
      Object.defineProperty(obj, 'kotlinHashCodeValue$', descriptor);
    }
    // Inline function 'kotlin.js.unsafeCast' call
    return obj['kotlinHashCodeValue$'];
  }
  function calculateRandomHash() {
    // Inline function 'kotlin.js.jsBitwiseOr' call
    return Math.random() * 4.294967296E9 | 0;
  }
  function toString_1(o) {
    var tmp;
    if (o == null) {
      tmp = 'null';
    } else if (isArrayish(o)) {
      tmp = '[...]';
    } else if (!(typeof o.toString === 'function')) {
      tmp = anyToString(o);
    } else {
      // Inline function 'kotlin.js.unsafeCast' call
      tmp = o.toString();
    }
    return tmp;
  }
  function anyToString(o) {
    return Object.prototype.toString.call(o);
  }
  function objectCreate(proto) {
    proto = proto === VOID ? null : proto;
    return Object.create(proto);
  }
  function defineProp(obj, name, getter, setter) {
    return Object.defineProperty(obj, name, {configurable: true, get: getter, set: setter});
  }
  function hashCode(obj) {
    if (obj == null)
      return 0;
    var typeOf = typeof obj;
    var tmp;
    switch (typeOf) {
      case 'object':
        tmp = 'function' === typeof obj.hashCode ? obj.hashCode() : getObjectHashCode(obj);
        break;
      case 'function':
        tmp = getObjectHashCode(obj);
        break;
      case 'number':
        tmp = getNumberHashCode(obj);
        break;
      case 'boolean':
        // Inline function 'kotlin.js.unsafeCast' call

        tmp = getBooleanHashCode(obj);
        break;
      case 'string':
        tmp = getStringHashCode(String(obj));
        break;
      case 'bigint':
        tmp = getBigIntHashCode(obj);
        break;
      case 'symbol':
        tmp = getSymbolHashCode(obj);
        break;
      default:
        tmp = function () {
          throw new Error('Unexpected typeof `' + typeOf + '`');
        }();
        break;
    }
    return tmp;
  }
  function getBooleanHashCode(value) {
    return value ? 1231 : 1237;
  }
  function getStringHashCode(str) {
    var hash = 0;
    var length = str.length;
    var inductionVariable = 0;
    var last = length - 1 | 0;
    if (inductionVariable <= last)
      do {
        var i = inductionVariable;
        inductionVariable = inductionVariable + 1 | 0;
        // Inline function 'kotlin.js.asDynamic' call
        var code = str.charCodeAt(i);
        hash = imul(hash, 31) + code | 0;
      }
       while (!(i === last));
    return hash;
  }
  function getBigIntHashCode(value) {
    var shiftNumber = BigInt(32);
    var MASK = BigInt(4.294967295E9);
    var bigNumber = value < 0 ? -value : value;
    var hashCode = 0;
    var signum = value < 0 ? -1 : 1;
    while (bigNumber != 0) {
      // Inline function 'kotlin.js.unsafeCast' call
      var chunk = Number(bigNumber & MASK);
      hashCode = imul(31, hashCode) + chunk | 0;
      bigNumber = bigNumber >> shiftNumber;
    }
    return imul(hashCode, signum);
  }
  function getSymbolHashCode(value) {
    var hashCodeMap = symbolIsSharable(value) ? getSymbolMap() : getSymbolWeakMap();
    var cachedHashCode = hashCodeMap.get(value);
    if (cachedHashCode !== VOID)
      return cachedHashCode;
    var hash = calculateRandomHash();
    hashCodeMap.set(value, hash);
    return hash;
  }
  function symbolIsSharable(symbol) {
    return Symbol.keyFor(symbol) != VOID;
  }
  function getSymbolMap() {
    if (symbolMap === VOID) {
      symbolMap = new Map();
    }
    return symbolMap;
  }
  function getSymbolWeakMap() {
    if (symbolWeakMap === VOID) {
      symbolWeakMap = new WeakMap();
    }
    return symbolWeakMap;
  }
  var symbolMap;
  var symbolWeakMap;
  function equals(obj1, obj2) {
    if (obj1 == null) {
      return obj2 == null;
    }
    if (obj2 == null) {
      return false;
    }
    if (typeof obj1 === 'object' && typeof obj1.equals === 'function') {
      return obj1.equals(obj2);
    }
    if (obj1 !== obj1) {
      return obj2 !== obj2;
    }
    if (typeof obj1 === 'number' && typeof obj2 === 'number') {
      var tmp;
      if (obj1 === obj2) {
        var tmp_0;
        if (obj1 !== 0) {
          tmp_0 = true;
        } else {
          // Inline function 'kotlin.js.asDynamic' call
          var tmp_1 = 1 / obj1;
          // Inline function 'kotlin.js.asDynamic' call
          tmp_0 = tmp_1 === 1 / obj2;
        }
        tmp = tmp_0;
      } else {
        tmp = false;
      }
      return tmp;
    }
    return obj1 === obj2;
  }
  function unboxIntrinsic(x) {
    var message = 'Should be lowered';
    throw IllegalStateException_init_$Create$_0(toString_1(message));
  }
  function captureStack(instance, constructorFunction) {
    if (Error.captureStackTrace != null) {
      Error.captureStackTrace(instance, constructorFunction);
    } else {
      // Inline function 'kotlin.js.asDynamic' call
      instance.stack = (new Error()).stack;
    }
  }
  function protoOf(constructor) {
    return constructor.prototype;
  }
  function defineMessage(message, cause) {
    var tmp;
    if (isUndefined(message)) {
      var tmp_0;
      if (isUndefined(cause)) {
        tmp_0 = message;
      } else {
        var tmp1_elvis_lhs = cause == null ? null : cause.toString();
        tmp_0 = tmp1_elvis_lhs == null ? VOID : tmp1_elvis_lhs;
      }
      tmp = tmp_0;
    } else {
      tmp = message == null ? VOID : message;
    }
    return tmp;
  }
  function isUndefined(value) {
    return value === VOID;
  }
  function extendThrowable(this_, message, cause) {
    defineFieldOnInstance(this_, 'message', defineMessage(message, cause));
    defineFieldOnInstance(this_, 'cause', cause);
    defineFieldOnInstance(this_, 'name', Object.getPrototypeOf(this_).constructor.name);
  }
  function defineFieldOnInstance(this_, name, value) {
    Object.defineProperty(this_, name, {configurable: true, writable: true, value: value});
  }
  function ensureNotNull(v) {
    var tmp;
    if (v == null) {
      THROW_NPE();
    } else {
      tmp = v;
    }
    return tmp;
  }
  function THROW_NPE() {
    throw NullPointerException_init_$Create$();
  }
  function THROW_CCE() {
    throw ClassCastException_init_$Create$();
  }
  function get_ZERO() {
    _init_properties_longJs_kt__elc2w5();
    return ZERO;
  }
  var ZERO;
  function get_ONE() {
    _init_properties_longJs_kt__elc2w5();
    return ONE;
  }
  var ONE;
  function get_NEG_ONE() {
    _init_properties_longJs_kt__elc2w5();
    return NEG_ONE;
  }
  var NEG_ONE;
  function get_MAX_VALUE() {
    _init_properties_longJs_kt__elc2w5();
    return MAX_VALUE;
  }
  var MAX_VALUE;
  function get_MIN_VALUE() {
    _init_properties_longJs_kt__elc2w5();
    return MIN_VALUE;
  }
  var MIN_VALUE;
  function get_TWO_PWR_24_() {
    _init_properties_longJs_kt__elc2w5();
    return TWO_PWR_24_;
  }
  var TWO_PWR_24_;
  function compare(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    if (equalsLong(_this__u8e3s4, other)) {
      return 0;
    }
    var thisNeg = isNegative(_this__u8e3s4);
    var otherNeg = isNegative(other);
    return thisNeg && !otherNeg ? -1 : !thisNeg && otherNeg ? 1 : isNegative(subtract(_this__u8e3s4, other)) ? -1 : 1;
  }
  function add(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    var a48 = _this__u8e3s4.u_1 >>> 16 | 0;
    var a32 = _this__u8e3s4.u_1 & 65535;
    var a16 = _this__u8e3s4.t_1 >>> 16 | 0;
    var a00 = _this__u8e3s4.t_1 & 65535;
    var b48 = other.u_1 >>> 16 | 0;
    var b32 = other.u_1 & 65535;
    var b16 = other.t_1 >>> 16 | 0;
    var b00 = other.t_1 & 65535;
    var c48 = 0;
    var c32 = 0;
    var c16 = 0;
    var c00 = 0;
    c00 = c00 + (a00 + b00 | 0) | 0;
    c16 = c16 + (c00 >>> 16 | 0) | 0;
    c00 = c00 & 65535;
    c16 = c16 + (a16 + b16 | 0) | 0;
    c32 = c32 + (c16 >>> 16 | 0) | 0;
    c16 = c16 & 65535;
    c32 = c32 + (a32 + b32 | 0) | 0;
    c48 = c48 + (c32 >>> 16 | 0) | 0;
    c32 = c32 & 65535;
    c48 = c48 + (a48 + b48 | 0) | 0;
    c48 = c48 & 65535;
    return new Long(c16 << 16 | c00, c48 << 16 | c32);
  }
  function subtract(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    return add(_this__u8e3s4, other.x());
  }
  function multiply(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    if (isZero(_this__u8e3s4)) {
      return get_ZERO();
    } else if (isZero(other)) {
      return get_ZERO();
    }
    if (equalsLong(_this__u8e3s4, get_MIN_VALUE())) {
      return isOdd(other) ? get_MIN_VALUE() : get_ZERO();
    } else if (equalsLong(other, get_MIN_VALUE())) {
      return isOdd(_this__u8e3s4) ? get_MIN_VALUE() : get_ZERO();
    }
    if (isNegative(_this__u8e3s4)) {
      var tmp;
      if (isNegative(other)) {
        tmp = multiply(negate(_this__u8e3s4), negate(other));
      } else {
        tmp = negate(multiply(negate(_this__u8e3s4), other));
      }
      return tmp;
    } else if (isNegative(other)) {
      return negate(multiply(_this__u8e3s4, negate(other)));
    }
    if (lessThan(_this__u8e3s4, get_TWO_PWR_24_()) && lessThan(other, get_TWO_PWR_24_())) {
      return fromNumber(toNumber(_this__u8e3s4) * toNumber(other));
    }
    var a48 = _this__u8e3s4.u_1 >>> 16 | 0;
    var a32 = _this__u8e3s4.u_1 & 65535;
    var a16 = _this__u8e3s4.t_1 >>> 16 | 0;
    var a00 = _this__u8e3s4.t_1 & 65535;
    var b48 = other.u_1 >>> 16 | 0;
    var b32 = other.u_1 & 65535;
    var b16 = other.t_1 >>> 16 | 0;
    var b00 = other.t_1 & 65535;
    var c48 = 0;
    var c32 = 0;
    var c16 = 0;
    var c00 = 0;
    c00 = c00 + imul(a00, b00) | 0;
    c16 = c16 + (c00 >>> 16 | 0) | 0;
    c00 = c00 & 65535;
    c16 = c16 + imul(a16, b00) | 0;
    c32 = c32 + (c16 >>> 16 | 0) | 0;
    c16 = c16 & 65535;
    c16 = c16 + imul(a00, b16) | 0;
    c32 = c32 + (c16 >>> 16 | 0) | 0;
    c16 = c16 & 65535;
    c32 = c32 + imul(a32, b00) | 0;
    c48 = c48 + (c32 >>> 16 | 0) | 0;
    c32 = c32 & 65535;
    c32 = c32 + imul(a16, b16) | 0;
    c48 = c48 + (c32 >>> 16 | 0) | 0;
    c32 = c32 & 65535;
    c32 = c32 + imul(a00, b32) | 0;
    c48 = c48 + (c32 >>> 16 | 0) | 0;
    c32 = c32 & 65535;
    c48 = c48 + (((imul(a48, b00) + imul(a32, b16) | 0) + imul(a16, b32) | 0) + imul(a00, b48) | 0) | 0;
    c48 = c48 & 65535;
    return new Long(c16 << 16 | c00, c48 << 16 | c32);
  }
  function divide(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    if (isZero(other)) {
      throw Exception_init_$Create$_0('division by zero');
    } else if (isZero(_this__u8e3s4)) {
      return get_ZERO();
    }
    if (equalsLong(_this__u8e3s4, get_MIN_VALUE())) {
      if (equalsLong(other, get_ONE()) || equalsLong(other, get_NEG_ONE())) {
        return get_MIN_VALUE();
      } else if (equalsLong(other, get_MIN_VALUE())) {
        return get_ONE();
      } else {
        var halfThis = shiftRight(_this__u8e3s4, 1);
        var approx = shiftLeft(halfThis.w(other), 1);
        if (equalsLong(approx, get_ZERO())) {
          return isNegative(other) ? get_ONE() : get_NEG_ONE();
        } else {
          var rem = subtract(_this__u8e3s4, multiply(other, approx));
          return add(approx, rem.w(other));
        }
      }
    } else if (equalsLong(other, get_MIN_VALUE())) {
      return get_ZERO();
    }
    if (isNegative(_this__u8e3s4)) {
      var tmp;
      if (isNegative(other)) {
        tmp = negate(_this__u8e3s4).w(negate(other));
      } else {
        tmp = negate(negate(_this__u8e3s4).w(other));
      }
      return tmp;
    } else if (isNegative(other)) {
      return negate(_this__u8e3s4.w(negate(other)));
    }
    var res = get_ZERO();
    var rem_0 = _this__u8e3s4;
    while (greaterThanOrEqual(rem_0, other)) {
      var approxDouble = toNumber(rem_0) / toNumber(other);
      var approx2 = Math.max(1.0, Math.floor(approxDouble));
      var log2 = Math.ceil(Math.log(approx2) / Math.LN2);
      var delta = log2 <= 48 ? 1.0 : Math.pow(2.0, log2 - 48);
      var approxRes = fromNumber(approx2);
      var approxRem = multiply(approxRes, other);
      while (isNegative(approxRem) || greaterThan(approxRem, rem_0)) {
        approx2 = approx2 - delta;
        approxRes = fromNumber(approx2);
        approxRem = multiply(approxRes, other);
      }
      if (isZero(approxRes)) {
        approxRes = get_ONE();
      }
      res = add(res, approxRes);
      rem_0 = subtract(rem_0, approxRem);
    }
    return res;
  }
  function shiftLeft(_this__u8e3s4, numBits) {
    _init_properties_longJs_kt__elc2w5();
    var numBits_0 = numBits & 63;
    if (numBits_0 === 0) {
      return _this__u8e3s4;
    } else {
      if (numBits_0 < 32) {
        return new Long(_this__u8e3s4.t_1 << numBits_0, _this__u8e3s4.u_1 << numBits_0 | (_this__u8e3s4.t_1 >>> (32 - numBits_0 | 0) | 0));
      } else {
        return new Long(0, _this__u8e3s4.t_1 << (numBits_0 - 32 | 0));
      }
    }
  }
  function shiftRight(_this__u8e3s4, numBits) {
    _init_properties_longJs_kt__elc2w5();
    var numBits_0 = numBits & 63;
    if (numBits_0 === 0) {
      return _this__u8e3s4;
    } else {
      if (numBits_0 < 32) {
        return new Long(_this__u8e3s4.t_1 >>> numBits_0 | 0 | _this__u8e3s4.u_1 << (32 - numBits_0 | 0), _this__u8e3s4.u_1 >> numBits_0);
      } else {
        return new Long(_this__u8e3s4.u_1 >> (numBits_0 - 32 | 0), _this__u8e3s4.u_1 >= 0 ? 0 : -1);
      }
    }
  }
  function toNumber(_this__u8e3s4) {
    _init_properties_longJs_kt__elc2w5();
    return _this__u8e3s4.u_1 * 4.294967296E9 + getLowBitsUnsigned(_this__u8e3s4);
  }
  function toStringImpl(_this__u8e3s4, radix) {
    _init_properties_longJs_kt__elc2w5();
    if (radix < 2 || 36 < radix) {
      throw Exception_init_$Create$_0('radix out of range: ' + radix);
    }
    if (isZero(_this__u8e3s4)) {
      return '0';
    }
    if (isNegative(_this__u8e3s4)) {
      if (equalsLong(_this__u8e3s4, get_MIN_VALUE())) {
        var radixLong = fromInt(radix);
        var div = _this__u8e3s4.w(radixLong);
        var rem = subtract(multiply(div, radixLong), _this__u8e3s4).z();
        var tmp = toStringImpl(div, radix);
        // Inline function 'kotlin.js.asDynamic' call
        // Inline function 'kotlin.js.unsafeCast' call
        return tmp + rem.toString(radix);
      } else {
        return '-' + toStringImpl(negate(_this__u8e3s4), radix);
      }
    }
    var digitsPerTime = radix === 2 ? 31 : radix <= 10 ? 9 : radix <= 21 ? 7 : radix <= 35 ? 6 : 5;
    var radixToPower = fromNumber(Math.pow(radix, digitsPerTime));
    var rem_0 = _this__u8e3s4;
    var result = '';
    while (true) {
      var remDiv = rem_0.w(radixToPower);
      var intval = subtract(rem_0, multiply(remDiv, radixToPower)).z();
      // Inline function 'kotlin.js.asDynamic' call
      // Inline function 'kotlin.js.unsafeCast' call
      var digits = intval.toString(radix);
      rem_0 = remDiv;
      if (isZero(rem_0)) {
        return digits + result;
      } else {
        while (digits.length < digitsPerTime) {
          digits = '0' + digits;
        }
        result = digits + result;
      }
    }
  }
  function equalsLong(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    return _this__u8e3s4.u_1 === other.u_1 && _this__u8e3s4.t_1 === other.t_1;
  }
  function hashCode_0(l) {
    _init_properties_longJs_kt__elc2w5();
    return l.t_1 ^ l.u_1;
  }
  function fromInt(value) {
    _init_properties_longJs_kt__elc2w5();
    return new Long(value, value < 0 ? -1 : 0);
  }
  function isNegative(_this__u8e3s4) {
    _init_properties_longJs_kt__elc2w5();
    return _this__u8e3s4.u_1 < 0;
  }
  function isZero(_this__u8e3s4) {
    _init_properties_longJs_kt__elc2w5();
    return _this__u8e3s4.u_1 === 0 && _this__u8e3s4.t_1 === 0;
  }
  function isOdd(_this__u8e3s4) {
    _init_properties_longJs_kt__elc2w5();
    return (_this__u8e3s4.t_1 & 1) === 1;
  }
  function negate(_this__u8e3s4) {
    _init_properties_longJs_kt__elc2w5();
    return _this__u8e3s4.x();
  }
  function lessThan(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    return compare(_this__u8e3s4, other) < 0;
  }
  function fromNumber(value) {
    _init_properties_longJs_kt__elc2w5();
    if (isNaN_0(value)) {
      return get_ZERO();
    } else if (value <= -9.223372036854776E18) {
      return get_MIN_VALUE();
    } else if (value + 1 >= 9.223372036854776E18) {
      return get_MAX_VALUE();
    } else if (value < 0) {
      return negate(fromNumber(-value));
    } else {
      var twoPwr32 = 4.294967296E9;
      // Inline function 'kotlin.js.jsBitwiseOr' call
      var tmp = value % twoPwr32 | 0;
      // Inline function 'kotlin.js.jsBitwiseOr' call
      var tmp$ret$1 = value / twoPwr32 | 0;
      return new Long(tmp, tmp$ret$1);
    }
  }
  function greaterThan(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    return compare(_this__u8e3s4, other) > 0;
  }
  function greaterThanOrEqual(_this__u8e3s4, other) {
    _init_properties_longJs_kt__elc2w5();
    return compare(_this__u8e3s4, other) >= 0;
  }
  function getLowBitsUnsigned(_this__u8e3s4) {
    _init_properties_longJs_kt__elc2w5();
    return _this__u8e3s4.t_1 >= 0 ? _this__u8e3s4.t_1 : 4.294967296E9 + _this__u8e3s4.t_1;
  }
  var properties_initialized_longJs_kt_4syf89;
  function _init_properties_longJs_kt__elc2w5() {
    if (!properties_initialized_longJs_kt_4syf89) {
      properties_initialized_longJs_kt_4syf89 = true;
      ZERO = fromInt(0);
      ONE = fromInt(1);
      NEG_ONE = fromInt(-1);
      MAX_VALUE = new Long(-1, 2147483647);
      MIN_VALUE = new Long(0, -2147483648);
      TWO_PWR_24_ = fromInt(16777216);
    }
  }
  function createMetadata(kind, name, defaultConstructor, associatedObjectKey, associatedObjects, suspendArity) {
    var undef = VOID;
    var iid = kind === 'interface' ? generateInterfaceId() : VOID;
    return {kind: kind, simpleName: name, associatedObjectKey: associatedObjectKey, associatedObjects: associatedObjects, suspendArity: suspendArity, $kClass$: undef, defaultConstructor: defaultConstructor, iid: iid};
  }
  function generateInterfaceId() {
    if (globalInterfaceId === VOID) {
      globalInterfaceId = 0;
    }
    // Inline function 'kotlin.js.unsafeCast' call
    globalInterfaceId = globalInterfaceId + 1 | 0;
    // Inline function 'kotlin.js.unsafeCast' call
    return globalInterfaceId;
  }
  var globalInterfaceId;
  function initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    if (!(parent == null)) {
      ctor.prototype = Object.create(parent.prototype);
      ctor.prototype.constructor = ctor;
    }
    var metadata = createMetadata(kind, name, defaultConstructor, associatedObjectKey, associatedObjects, suspendArity);
    ctor.$metadata$ = metadata;
    if (!(interfaces == null)) {
      var receiver = !equals(metadata.iid, VOID) ? ctor : ctor.prototype;
      receiver.$imask$ = implement(interfaces);
    }
  }
  function initMetadataForClass(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    var kind = 'class';
    initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
  }
  function initMetadataForObject(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    var kind = 'object';
    initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
  }
  function initMetadataForInterface(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    var kind = 'interface';
    initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
  }
  function initMetadataForLambda(ctor, parent, interfaces, suspendArity) {
    initMetadataForClass(ctor, 'Lambda', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function initMetadataForCoroutine(ctor, parent, interfaces, suspendArity) {
    initMetadataForClass(ctor, 'Coroutine', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function initMetadataForFunctionReference(ctor, parent, interfaces, suspendArity) {
    initMetadataForClass(ctor, 'FunctionReference', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function initMetadataForCompanion(ctor, parent, interfaces, suspendArity) {
    initMetadataForObject(ctor, 'Companion', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function numberToInt(a) {
    var tmp;
    if (a instanceof Long) {
      tmp = a.z();
    } else {
      tmp = doubleToInt(a);
    }
    return tmp;
  }
  function doubleToInt(a) {
    var tmp;
    if (a > 2147483647) {
      tmp = 2147483647;
    } else if (a < -2147483648) {
      tmp = -2147483648;
    } else {
      // Inline function 'kotlin.js.jsBitwiseOr' call
      tmp = a | 0;
    }
    return tmp;
  }
  function numberRangeToNumber(start, endInclusive) {
    return new IntRange(start, endInclusive);
  }
  function isArrayish(o) {
    return isJsArray(o) || isView(o);
  }
  function isJsArray(obj) {
    // Inline function 'kotlin.js.unsafeCast' call
    return Array.isArray(obj);
  }
  function isInterface(obj, iface) {
    return isInterfaceImpl(obj, iface.$metadata$.iid);
  }
  function isInterfaceImpl(obj, iface) {
    // Inline function 'kotlin.js.unsafeCast' call
    var tmp0_elvis_lhs = obj.$imask$;
    var tmp;
    if (tmp0_elvis_lhs == null) {
      return false;
    } else {
      tmp = tmp0_elvis_lhs;
    }
    var mask = tmp;
    return isBitSet(mask, iface);
  }
  function isCharSequence(value) {
    return typeof value === 'string' || isInterface(value, CharSequence);
  }
  function get_VOID() {
    _init_properties_void_kt__3zg9as();
    return VOID;
  }
  var VOID;
  var properties_initialized_void_kt_e4ret2;
  function _init_properties_void_kt__3zg9as() {
    if (!properties_initialized_void_kt_e4ret2) {
      properties_initialized_void_kt_e4ret2 = true;
      VOID = void 0;
    }
  }
  function asList(_this__u8e3s4) {
    return new asList$1(_this__u8e3s4);
  }
  function asList$1($this_asList) {
    this.f1_1 = $this_asList;
    AbstractList.call(this);
  }
  protoOf(asList$1).h = function () {
    return this.f1_1.length;
  };
  protoOf(asList$1).e = function (index) {
    var tmp;
    if (0 <= index ? index <= get_lastIndex(this) : false) {
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      tmp = this.f1_1.item(index);
    } else {
      throw IndexOutOfBoundsException_init_$Create$_0('index ' + index + ' is not in range [0..' + get_lastIndex(this) + ']');
    }
    return tmp;
  };
  function isNaN_0(_this__u8e3s4) {
    return !(_this__u8e3s4 === _this__u8e3s4);
  }
  function Unit() {
  }
  protoOf(Unit).toString = function () {
    return 'kotlin.Unit';
  };
  var Unit_instance;
  function Unit_getInstance() {
    return Unit_instance;
  }
  function collectionToArray(collection) {
    return collectionToArrayCommonImpl(collection);
  }
  function checkIndexOverflow(index) {
    if (index < 0) {
      throwIndexOverflow();
    }
    return index;
  }
  function AbstractMutableCollection() {
    AbstractCollection.call(this);
  }
  protoOf(AbstractMutableCollection).h1 = function (elements) {
    this.i1();
    var modified = false;
    var _iterator__ex2g4s = elements.b();
    while (_iterator__ex2g4s.c()) {
      var element = _iterator__ex2g4s.d();
      if (this.g1(element))
        modified = true;
    }
    return modified;
  };
  protoOf(AbstractMutableCollection).toJSON = function () {
    return this.toArray();
  };
  protoOf(AbstractMutableCollection).i1 = function () {
  };
  function IteratorImpl($outer) {
    this.l1_1 = $outer;
    this.j1_1 = 0;
    this.k1_1 = -1;
  }
  protoOf(IteratorImpl).c = function () {
    return this.j1_1 < this.l1_1.h();
  };
  protoOf(IteratorImpl).d = function () {
    if (!this.c())
      throw NoSuchElementException_init_$Create$();
    var tmp = this;
    var _unary__edvuaz = this.j1_1;
    this.j1_1 = _unary__edvuaz + 1 | 0;
    tmp.k1_1 = _unary__edvuaz;
    return this.l1_1.e(this.k1_1);
  };
  function ListIteratorImpl($outer, index) {
    this.p1_1 = $outer;
    IteratorImpl.call(this, $outer);
    Companion_instance_2.r1(index, this.p1_1.h());
    this.j1_1 = index;
  }
  protoOf(ListIteratorImpl).s1 = function () {
    return this.j1_1 > 0;
  };
  protoOf(ListIteratorImpl).t1 = function () {
    return this.j1_1;
  };
  protoOf(ListIteratorImpl).u1 = function () {
    if (!this.s1())
      throw NoSuchElementException_init_$Create$();
    var tmp = this;
    this.j1_1 = this.j1_1 - 1 | 0;
    tmp.k1_1 = this.j1_1;
    return this.p1_1.e(this.k1_1);
  };
  function SubList(list, fromIndex, toIndex) {
    AbstractMutableList.call(this);
    this.w1_1 = list;
    this.x1_1 = fromIndex;
    this.y1_1 = 0;
    Companion_instance_2.z1(this.x1_1, toIndex, this.w1_1.h());
    this.y1_1 = toIndex - this.x1_1 | 0;
  }
  protoOf(SubList).a2 = function (index, element) {
    Companion_instance_2.r1(index, this.y1_1);
    this.w1_1.a2(this.x1_1 + index | 0, element);
    this.y1_1 = this.y1_1 + 1 | 0;
  };
  protoOf(SubList).e = function (index) {
    Companion_instance_2.c2(index, this.y1_1);
    return this.w1_1.e(this.x1_1 + index | 0);
  };
  protoOf(SubList).h = function () {
    return this.y1_1;
  };
  protoOf(SubList).i1 = function () {
    return this.w1_1.i1();
  };
  function AbstractMutableList() {
    AbstractMutableCollection.call(this);
    this.b2_1 = 0;
  }
  protoOf(AbstractMutableList).g1 = function (element) {
    this.i1();
    this.a2(this.h(), element);
    return true;
  };
  protoOf(AbstractMutableList).b = function () {
    return new IteratorImpl(this);
  };
  protoOf(AbstractMutableList).i = function (element) {
    return this.k(element) >= 0;
  };
  protoOf(AbstractMutableList).k = function (element) {
    var tmp$ret$1;
    $l$block: {
      // Inline function 'kotlin.collections.indexOfFirst' call
      var index = 0;
      var _iterator__ex2g4s = this.b();
      while (_iterator__ex2g4s.c()) {
        var item = _iterator__ex2g4s.d();
        if (equals(item, element)) {
          tmp$ret$1 = index;
          break $l$block;
        }
        index = index + 1 | 0;
      }
      tmp$ret$1 = -1;
    }
    return tmp$ret$1;
  };
  protoOf(AbstractMutableList).l = function (element) {
    var tmp$ret$1;
    $l$block: {
      // Inline function 'kotlin.collections.indexOfLast' call
      var iterator = this.n(this.h());
      while (iterator.s1()) {
        var it = iterator.u1();
        if (equals(it, element)) {
          tmp$ret$1 = iterator.t1();
          break $l$block;
        }
      }
      tmp$ret$1 = -1;
    }
    return tmp$ret$1;
  };
  protoOf(AbstractMutableList).m = function () {
    return this.n(0);
  };
  protoOf(AbstractMutableList).n = function (index) {
    return new ListIteratorImpl(this, index);
  };
  protoOf(AbstractMutableList).o = function (fromIndex, toIndex) {
    return new SubList(this, fromIndex, toIndex);
  };
  protoOf(AbstractMutableList).equals = function (other) {
    if (other === this)
      return true;
    if (!(!(other == null) ? isInterface(other, KtList) : false))
      return false;
    return Companion_instance_2.d2(this, other);
  };
  protoOf(AbstractMutableList).hashCode = function () {
    return Companion_instance_2.e2(this);
  };
  function Companion_1() {
    Companion_instance_1 = this;
    var tmp = this;
    // Inline function 'kotlin.also' call
    var this_0 = ArrayList_init_$Create$_0(0);
    this_0.d1_1 = true;
    tmp.f2_1 = this_0;
  }
  var Companion_instance_1;
  function Companion_getInstance_1() {
    if (Companion_instance_1 == null)
      new Companion_1();
    return Companion_instance_1;
  }
  function ArrayList_init_$Init$($this) {
    // Inline function 'kotlin.emptyArray' call
    var tmp$ret$0 = [];
    ArrayList.call($this, tmp$ret$0);
    return $this;
  }
  function ArrayList_init_$Create$() {
    return ArrayList_init_$Init$(objectCreate(protoOf(ArrayList)));
  }
  function ArrayList_init_$Init$_0(initialCapacity, $this) {
    // Inline function 'kotlin.emptyArray' call
    var tmp$ret$0 = [];
    ArrayList.call($this, tmp$ret$0);
    // Inline function 'kotlin.require' call
    if (!(initialCapacity >= 0)) {
      var message = 'Negative initial capacity: ' + initialCapacity;
      throw IllegalArgumentException_init_$Create$_0(toString_1(message));
    }
    return $this;
  }
  function ArrayList_init_$Create$_0(initialCapacity) {
    return ArrayList_init_$Init$_0(initialCapacity, objectCreate(protoOf(ArrayList)));
  }
  function increaseLength($this, amount) {
    var previous = $this.h();
    // Inline function 'kotlin.js.asDynamic' call
    $this.c1_1.length = $this.h() + amount | 0;
    return previous;
  }
  function rangeCheck($this, index) {
    // Inline function 'kotlin.apply' call
    Companion_instance_2.c2(index, $this.h());
    return index;
  }
  function insertionRangeCheck($this, index) {
    // Inline function 'kotlin.apply' call
    Companion_instance_2.r1(index, $this.h());
    return index;
  }
  function ArrayList(array) {
    Companion_getInstance_1();
    AbstractMutableList.call(this);
    this.c1_1 = array;
    this.d1_1 = false;
  }
  protoOf(ArrayList).e1 = function () {
    this.i1();
    this.d1_1 = true;
    return this.h() > 0 ? this : Companion_getInstance_1().f2_1;
  };
  protoOf(ArrayList).h = function () {
    return this.c1_1.length;
  };
  protoOf(ArrayList).e = function (index) {
    var tmp = this.c1_1[rangeCheck(this, index)];
    return (tmp == null ? true : !(tmp == null)) ? tmp : THROW_CCE();
  };
  protoOf(ArrayList).g1 = function (element) {
    this.i1();
    // Inline function 'kotlin.js.asDynamic' call
    this.c1_1.push(element);
    this.b2_1 = this.b2_1 + 1 | 0;
    return true;
  };
  protoOf(ArrayList).a2 = function (index, element) {
    this.i1();
    // Inline function 'kotlin.js.asDynamic' call
    this.c1_1.splice(insertionRangeCheck(this, index), 0, element);
    this.b2_1 = this.b2_1 + 1 | 0;
  };
  protoOf(ArrayList).h1 = function (elements) {
    this.i1();
    if (elements.f())
      return false;
    var offset = increaseLength(this, elements.h());
    // Inline function 'kotlin.collections.forEachIndexed' call
    var index = 0;
    var _iterator__ex2g4s = elements.b();
    while (_iterator__ex2g4s.c()) {
      var item = _iterator__ex2g4s.d();
      var _unary__edvuaz = index;
      index = _unary__edvuaz + 1 | 0;
      var index_0 = checkIndexOverflow(_unary__edvuaz);
      this.c1_1[offset + index_0 | 0] = item;
    }
    this.b2_1 = this.b2_1 + 1 | 0;
    return true;
  };
  protoOf(ArrayList).k = function (element) {
    return indexOf(this.c1_1, element);
  };
  protoOf(ArrayList).l = function (element) {
    return lastIndexOf(this.c1_1, element);
  };
  protoOf(ArrayList).toString = function () {
    return arrayToString(this.c1_1);
  };
  protoOf(ArrayList).g2 = function () {
    return [].slice.call(this.c1_1);
  };
  protoOf(ArrayList).toArray = function () {
    return this.g2();
  };
  protoOf(ArrayList).i1 = function () {
    if (this.d1_1)
      throw UnsupportedOperationException_init_$Create$();
  };
  function Exception_init_$Init$($this) {
    extendThrowable($this);
    Exception.call($this);
    return $this;
  }
  function Exception_init_$Create$() {
    var tmp = Exception_init_$Init$(objectCreate(protoOf(Exception)));
    captureStack(tmp, Exception_init_$Create$);
    return tmp;
  }
  function Exception_init_$Init$_0(message, $this) {
    extendThrowable($this, message);
    Exception.call($this);
    return $this;
  }
  function Exception_init_$Create$_0(message) {
    var tmp = Exception_init_$Init$_0(message, objectCreate(protoOf(Exception)));
    captureStack(tmp, Exception_init_$Create$_0);
    return tmp;
  }
  function Exception() {
    captureStack(this, Exception);
  }
  function IllegalArgumentException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    IllegalArgumentException.call($this);
    return $this;
  }
  function IllegalArgumentException_init_$Create$() {
    var tmp = IllegalArgumentException_init_$Init$(objectCreate(protoOf(IllegalArgumentException)));
    captureStack(tmp, IllegalArgumentException_init_$Create$);
    return tmp;
  }
  function IllegalArgumentException_init_$Init$_0(message, $this) {
    RuntimeException_init_$Init$_0(message, $this);
    IllegalArgumentException.call($this);
    return $this;
  }
  function IllegalArgumentException_init_$Create$_0(message) {
    var tmp = IllegalArgumentException_init_$Init$_0(message, objectCreate(protoOf(IllegalArgumentException)));
    captureStack(tmp, IllegalArgumentException_init_$Create$_0);
    return tmp;
  }
  function IllegalArgumentException() {
    captureStack(this, IllegalArgumentException);
  }
  function IllegalStateException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    IllegalStateException.call($this);
    return $this;
  }
  function IllegalStateException_init_$Create$() {
    var tmp = IllegalStateException_init_$Init$(objectCreate(protoOf(IllegalStateException)));
    captureStack(tmp, IllegalStateException_init_$Create$);
    return tmp;
  }
  function IllegalStateException_init_$Init$_0(message, $this) {
    RuntimeException_init_$Init$_0(message, $this);
    IllegalStateException.call($this);
    return $this;
  }
  function IllegalStateException_init_$Create$_0(message) {
    var tmp = IllegalStateException_init_$Init$_0(message, objectCreate(protoOf(IllegalStateException)));
    captureStack(tmp, IllegalStateException_init_$Create$_0);
    return tmp;
  }
  function IllegalStateException() {
    captureStack(this, IllegalStateException);
  }
  function UnsupportedOperationException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    UnsupportedOperationException.call($this);
    return $this;
  }
  function UnsupportedOperationException_init_$Create$() {
    var tmp = UnsupportedOperationException_init_$Init$(objectCreate(protoOf(UnsupportedOperationException)));
    captureStack(tmp, UnsupportedOperationException_init_$Create$);
    return tmp;
  }
  function UnsupportedOperationException() {
    captureStack(this, UnsupportedOperationException);
  }
  function RuntimeException_init_$Init$($this) {
    Exception_init_$Init$($this);
    RuntimeException.call($this);
    return $this;
  }
  function RuntimeException_init_$Create$() {
    var tmp = RuntimeException_init_$Init$(objectCreate(protoOf(RuntimeException)));
    captureStack(tmp, RuntimeException_init_$Create$);
    return tmp;
  }
  function RuntimeException_init_$Init$_0(message, $this) {
    Exception_init_$Init$_0(message, $this);
    RuntimeException.call($this);
    return $this;
  }
  function RuntimeException() {
    captureStack(this, RuntimeException);
  }
  function NoSuchElementException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    NoSuchElementException.call($this);
    return $this;
  }
  function NoSuchElementException_init_$Create$() {
    var tmp = NoSuchElementException_init_$Init$(objectCreate(protoOf(NoSuchElementException)));
    captureStack(tmp, NoSuchElementException_init_$Create$);
    return tmp;
  }
  function NoSuchElementException() {
    captureStack(this, NoSuchElementException);
  }
  function IndexOutOfBoundsException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    IndexOutOfBoundsException.call($this);
    return $this;
  }
  function IndexOutOfBoundsException_init_$Create$() {
    var tmp = IndexOutOfBoundsException_init_$Init$(objectCreate(protoOf(IndexOutOfBoundsException)));
    captureStack(tmp, IndexOutOfBoundsException_init_$Create$);
    return tmp;
  }
  function IndexOutOfBoundsException_init_$Init$_0(message, $this) {
    RuntimeException_init_$Init$_0(message, $this);
    IndexOutOfBoundsException.call($this);
    return $this;
  }
  function IndexOutOfBoundsException_init_$Create$_0(message) {
    var tmp = IndexOutOfBoundsException_init_$Init$_0(message, objectCreate(protoOf(IndexOutOfBoundsException)));
    captureStack(tmp, IndexOutOfBoundsException_init_$Create$_0);
    return tmp;
  }
  function IndexOutOfBoundsException() {
    captureStack(this, IndexOutOfBoundsException);
  }
  function ArithmeticException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    ArithmeticException.call($this);
    return $this;
  }
  function ArithmeticException_init_$Create$() {
    var tmp = ArithmeticException_init_$Init$(objectCreate(protoOf(ArithmeticException)));
    captureStack(tmp, ArithmeticException_init_$Create$);
    return tmp;
  }
  function ArithmeticException_init_$Init$_0(message, $this) {
    RuntimeException_init_$Init$_0(message, $this);
    ArithmeticException.call($this);
    return $this;
  }
  function ArithmeticException_init_$Create$_0(message) {
    var tmp = ArithmeticException_init_$Init$_0(message, objectCreate(protoOf(ArithmeticException)));
    captureStack(tmp, ArithmeticException_init_$Create$_0);
    return tmp;
  }
  function ArithmeticException() {
    captureStack(this, ArithmeticException);
  }
  function NullPointerException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    NullPointerException.call($this);
    return $this;
  }
  function NullPointerException_init_$Create$() {
    var tmp = NullPointerException_init_$Init$(objectCreate(protoOf(NullPointerException)));
    captureStack(tmp, NullPointerException_init_$Create$);
    return tmp;
  }
  function NullPointerException() {
    captureStack(this, NullPointerException);
  }
  function ClassCastException_init_$Init$($this) {
    RuntimeException_init_$Init$($this);
    ClassCastException.call($this);
    return $this;
  }
  function ClassCastException_init_$Create$() {
    var tmp = ClassCastException_init_$Init$(objectCreate(protoOf(ClassCastException)));
    captureStack(tmp, ClassCastException_init_$Create$);
    return tmp;
  }
  function ClassCastException() {
    captureStack(this, ClassCastException);
  }
  function StringBuilder_init_$Init$($this) {
    StringBuilder.call($this, '');
    return $this;
  }
  function StringBuilder_init_$Create$() {
    return StringBuilder_init_$Init$(objectCreate(protoOf(StringBuilder)));
  }
  function StringBuilder(content) {
    this.j2_1 = content;
  }
  protoOf(StringBuilder).k2 = function (value) {
    this.j2_1 = this.j2_1 + toString(value);
    return this;
  };
  protoOf(StringBuilder).a = function (value) {
    this.j2_1 = this.j2_1 + toString_0(value);
    return this;
  };
  protoOf(StringBuilder).toString = function () {
    return this.j2_1;
  };
  function AbstractCollection$toString$lambda(this$0) {
    return function (it) {
      return it === this$0 ? '(this Collection)' : toString_0(it);
    };
  }
  function AbstractCollection() {
  }
  protoOf(AbstractCollection).i = function (element) {
    var tmp$ret$0;
    $l$block_0: {
      // Inline function 'kotlin.collections.any' call
      var tmp;
      if (isInterface(this, Collection)) {
        tmp = this.f();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = false;
        break $l$block_0;
      }
      var _iterator__ex2g4s = this.b();
      while (_iterator__ex2g4s.c()) {
        var element_0 = _iterator__ex2g4s.d();
        if (equals(element_0, element)) {
          tmp$ret$0 = true;
          break $l$block_0;
        }
      }
      tmp$ret$0 = false;
    }
    return tmp$ret$0;
  };
  protoOf(AbstractCollection).j = function (elements) {
    var tmp$ret$0;
    $l$block_0: {
      // Inline function 'kotlin.collections.all' call
      var tmp;
      if (isInterface(elements, Collection)) {
        tmp = elements.f();
      } else {
        tmp = false;
      }
      if (tmp) {
        tmp$ret$0 = true;
        break $l$block_0;
      }
      var _iterator__ex2g4s = elements.b();
      while (_iterator__ex2g4s.c()) {
        var element = _iterator__ex2g4s.d();
        if (!this.i(element)) {
          tmp$ret$0 = false;
          break $l$block_0;
        }
      }
      tmp$ret$0 = true;
    }
    return tmp$ret$0;
  };
  protoOf(AbstractCollection).f = function () {
    return this.h() === 0;
  };
  protoOf(AbstractCollection).toString = function () {
    return joinToString_0(this, ', ', '[', ']', VOID, VOID, AbstractCollection$toString$lambda(this));
  };
  protoOf(AbstractCollection).toArray = function () {
    return collectionToArray(this);
  };
  function SubList_0(list, fromIndex, toIndex) {
    AbstractList.call(this);
    this.l2_1 = list;
    this.m2_1 = fromIndex;
    this.n2_1 = 0;
    Companion_instance_2.z1(this.m2_1, toIndex, this.l2_1.h());
    this.n2_1 = toIndex - this.m2_1 | 0;
  }
  protoOf(SubList_0).e = function (index) {
    Companion_instance_2.c2(index, this.n2_1);
    return this.l2_1.e(this.m2_1 + index | 0);
  };
  protoOf(SubList_0).h = function () {
    return this.n2_1;
  };
  protoOf(SubList_0).o = function (fromIndex, toIndex) {
    Companion_instance_2.z1(fromIndex, toIndex, this.n2_1);
    return new SubList_0(this.l2_1, this.m2_1 + fromIndex | 0, this.m2_1 + toIndex | 0);
  };
  function IteratorImpl_0($outer) {
    this.p2_1 = $outer;
    this.o2_1 = 0;
  }
  protoOf(IteratorImpl_0).c = function () {
    return this.o2_1 < this.p2_1.h();
  };
  protoOf(IteratorImpl_0).d = function () {
    if (!this.c())
      throw NoSuchElementException_init_$Create$();
    var _unary__edvuaz = this.o2_1;
    this.o2_1 = _unary__edvuaz + 1 | 0;
    return this.p2_1.e(_unary__edvuaz);
  };
  function ListIteratorImpl_0($outer, index) {
    this.s2_1 = $outer;
    IteratorImpl_0.call(this, $outer);
    Companion_instance_2.r1(index, this.s2_1.h());
    this.o2_1 = index;
  }
  protoOf(ListIteratorImpl_0).s1 = function () {
    return this.o2_1 > 0;
  };
  protoOf(ListIteratorImpl_0).t1 = function () {
    return this.o2_1;
  };
  protoOf(ListIteratorImpl_0).u1 = function () {
    if (!this.s1())
      throw NoSuchElementException_init_$Create$();
    this.o2_1 = this.o2_1 - 1 | 0;
    return this.s2_1.e(this.o2_1);
  };
  function Companion_2() {
    this.q1_1 = 2147483639;
  }
  protoOf(Companion_2).c2 = function (index, size) {
    if (index < 0 || index >= size) {
      throw IndexOutOfBoundsException_init_$Create$_0('index: ' + index + ', size: ' + size);
    }
  };
  protoOf(Companion_2).r1 = function (index, size) {
    if (index < 0 || index > size) {
      throw IndexOutOfBoundsException_init_$Create$_0('index: ' + index + ', size: ' + size);
    }
  };
  protoOf(Companion_2).z1 = function (fromIndex, toIndex, size) {
    if (fromIndex < 0 || toIndex > size) {
      throw IndexOutOfBoundsException_init_$Create$_0('fromIndex: ' + fromIndex + ', toIndex: ' + toIndex + ', size: ' + size);
    }
    if (fromIndex > toIndex) {
      throw IllegalArgumentException_init_$Create$_0('fromIndex: ' + fromIndex + ' > toIndex: ' + toIndex);
    }
  };
  protoOf(Companion_2).e2 = function (c) {
    var hashCode_0 = 1;
    var _iterator__ex2g4s = c.b();
    while (_iterator__ex2g4s.c()) {
      var e = _iterator__ex2g4s.d();
      var tmp = imul(31, hashCode_0);
      var tmp1_elvis_lhs = e == null ? null : hashCode(e);
      hashCode_0 = tmp + (tmp1_elvis_lhs == null ? 0 : tmp1_elvis_lhs) | 0;
    }
    return hashCode_0;
  };
  protoOf(Companion_2).d2 = function (c, other) {
    if (!(c.h() === other.h()))
      return false;
    var otherIterator = other.b();
    var _iterator__ex2g4s = c.b();
    while (_iterator__ex2g4s.c()) {
      var elem = _iterator__ex2g4s.d();
      var elemOther = otherIterator.d();
      if (!equals(elem, elemOther)) {
        return false;
      }
    }
    return true;
  };
  var Companion_instance_2;
  function Companion_getInstance_2() {
    return Companion_instance_2;
  }
  function AbstractList() {
    AbstractCollection.call(this);
  }
  protoOf(AbstractList).b = function () {
    return new IteratorImpl_0(this);
  };
  protoOf(AbstractList).k = function (element) {
    var tmp$ret$1;
    $l$block: {
      // Inline function 'kotlin.collections.indexOfFirst' call
      var index = 0;
      var _iterator__ex2g4s = this.b();
      while (_iterator__ex2g4s.c()) {
        var item = _iterator__ex2g4s.d();
        if (equals(item, element)) {
          tmp$ret$1 = index;
          break $l$block;
        }
        index = index + 1 | 0;
      }
      tmp$ret$1 = -1;
    }
    return tmp$ret$1;
  };
  protoOf(AbstractList).l = function (element) {
    var tmp$ret$1;
    $l$block: {
      // Inline function 'kotlin.collections.indexOfLast' call
      var iterator = this.n(this.h());
      while (iterator.s1()) {
        var it = iterator.u1();
        if (equals(it, element)) {
          tmp$ret$1 = iterator.t1();
          break $l$block;
        }
      }
      tmp$ret$1 = -1;
    }
    return tmp$ret$1;
  };
  protoOf(AbstractList).m = function () {
    return new ListIteratorImpl_0(this, 0);
  };
  protoOf(AbstractList).n = function (index) {
    return new ListIteratorImpl_0(this, index);
  };
  protoOf(AbstractList).o = function (fromIndex, toIndex) {
    return new SubList_0(this, fromIndex, toIndex);
  };
  protoOf(AbstractList).equals = function (other) {
    if (other === this)
      return true;
    if (!(!(other == null) ? isInterface(other, KtList) : false))
      return false;
    return Companion_instance_2.d2(this, other);
  };
  protoOf(AbstractList).hashCode = function () {
    return Companion_instance_2.e2(this);
  };
  function collectionToArrayCommonImpl(collection) {
    if (collection.f()) {
      // Inline function 'kotlin.emptyArray' call
      return [];
    }
    // Inline function 'kotlin.arrayOfNulls' call
    var size = collection.h();
    var destination = Array(size);
    var iterator = collection.b();
    var index = 0;
    while (iterator.c()) {
      var _unary__edvuaz = index;
      index = _unary__edvuaz + 1 | 0;
      destination[_unary__edvuaz] = iterator.d();
    }
    return destination;
  }
  function emptyList() {
    return EmptyList_getInstance();
  }
  function get_lastIndex(_this__u8e3s4) {
    return _this__u8e3s4.h() - 1 | 0;
  }
  function EmptyList() {
    EmptyList_instance = this;
    this.t2_1 = new Long(-1478467534, -1720727600);
  }
  protoOf(EmptyList).equals = function (other) {
    var tmp;
    if (!(other == null) ? isInterface(other, KtList) : false) {
      tmp = other.f();
    } else {
      tmp = false;
    }
    return tmp;
  };
  protoOf(EmptyList).hashCode = function () {
    return 1;
  };
  protoOf(EmptyList).toString = function () {
    return '[]';
  };
  protoOf(EmptyList).h = function () {
    return 0;
  };
  protoOf(EmptyList).f = function () {
    return true;
  };
  protoOf(EmptyList).u2 = function (element) {
    return false;
  };
  protoOf(EmptyList).i = function (element) {
    if (!false)
      return false;
    var tmp;
    if (false) {
      tmp = element;
    } else {
      tmp = THROW_CCE();
    }
    return this.u2(tmp);
  };
  protoOf(EmptyList).v2 = function (elements) {
    return elements.f();
  };
  protoOf(EmptyList).j = function (elements) {
    return this.v2(elements);
  };
  protoOf(EmptyList).e = function (index) {
    throw IndexOutOfBoundsException_init_$Create$_0("Empty list doesn't contain element at index " + index + '.');
  };
  protoOf(EmptyList).w2 = function (element) {
    return -1;
  };
  protoOf(EmptyList).k = function (element) {
    if (!false)
      return -1;
    var tmp;
    if (false) {
      tmp = element;
    } else {
      tmp = THROW_CCE();
    }
    return this.w2(tmp);
  };
  protoOf(EmptyList).x2 = function (element) {
    return -1;
  };
  protoOf(EmptyList).l = function (element) {
    if (!false)
      return -1;
    var tmp;
    if (false) {
      tmp = element;
    } else {
      tmp = THROW_CCE();
    }
    return this.x2(tmp);
  };
  protoOf(EmptyList).b = function () {
    return EmptyIterator_instance;
  };
  protoOf(EmptyList).m = function () {
    return EmptyIterator_instance;
  };
  protoOf(EmptyList).n = function (index) {
    if (!(index === 0))
      throw IndexOutOfBoundsException_init_$Create$_0('Index: ' + index);
    return EmptyIterator_instance;
  };
  protoOf(EmptyList).o = function (fromIndex, toIndex) {
    if (fromIndex === 0 && toIndex === 0)
      return this;
    throw IndexOutOfBoundsException_init_$Create$_0('fromIndex: ' + fromIndex + ', toIndex: ' + toIndex);
  };
  var EmptyList_instance;
  function EmptyList_getInstance() {
    if (EmptyList_instance == null)
      new EmptyList();
    return EmptyList_instance;
  }
  function EmptyIterator() {
  }
  protoOf(EmptyIterator).c = function () {
    return false;
  };
  protoOf(EmptyIterator).s1 = function () {
    return false;
  };
  protoOf(EmptyIterator).t1 = function () {
    return 0;
  };
  protoOf(EmptyIterator).d = function () {
    throw NoSuchElementException_init_$Create$();
  };
  protoOf(EmptyIterator).u1 = function () {
    throw NoSuchElementException_init_$Create$();
  };
  var EmptyIterator_instance;
  function EmptyIterator_getInstance() {
    return EmptyIterator_instance;
  }
  function throwIndexOverflow() {
    throw ArithmeticException_init_$Create$_0('Index overflow has happened.');
  }
  function collectionSizeOrDefault(_this__u8e3s4, default_0) {
    var tmp;
    if (isInterface(_this__u8e3s4, Collection)) {
      tmp = _this__u8e3s4.h();
    } else {
      tmp = default_0;
    }
    return tmp;
  }
  function addAll(_this__u8e3s4, elements) {
    if (isInterface(elements, Collection))
      return _this__u8e3s4.h1(elements);
    else {
      var result = false;
      var _iterator__ex2g4s = elements.b();
      while (_iterator__ex2g4s.c()) {
        var item = _iterator__ex2g4s.d();
        if (_this__u8e3s4.g1(item))
          result = true;
      }
      return result;
    }
  }
  function IntIterator() {
  }
  protoOf(IntIterator).d = function () {
    return this.y2();
  };
  function getProgressionLastElement(start, end, step) {
    var tmp;
    if (step > 0) {
      tmp = start >= end ? end : end - differenceModulo(end, start, step) | 0;
    } else if (step < 0) {
      tmp = start <= end ? end : end + differenceModulo(start, end, -step | 0) | 0;
    } else {
      throw IllegalArgumentException_init_$Create$_0('Step is zero.');
    }
    return tmp;
  }
  function differenceModulo(a, b, c) {
    return mod(mod(a, c) - mod(b, c) | 0, c);
  }
  function mod(a, b) {
    var mod = a % b | 0;
    return mod >= 0 ? mod : mod + b | 0;
  }
  function Companion_3() {
    Companion_instance_3 = this;
    this.g_1 = new IntRange(1, 0);
  }
  var Companion_instance_3;
  function Companion_getInstance_3() {
    if (Companion_instance_3 == null)
      new Companion_3();
    return Companion_instance_3;
  }
  function IntRange(start, endInclusive) {
    Companion_getInstance_3();
    IntProgression.call(this, start, endInclusive, 1);
  }
  protoOf(IntRange).f = function () {
    return this.c3_1 > this.d3_1;
  };
  protoOf(IntRange).equals = function (other) {
    var tmp;
    if (other instanceof IntRange) {
      tmp = this.f() && other.f() || (this.c3_1 === other.c3_1 && this.d3_1 === other.d3_1);
    } else {
      tmp = false;
    }
    return tmp;
  };
  protoOf(IntRange).hashCode = function () {
    return this.f() ? -1 : imul(31, this.c3_1) + this.d3_1 | 0;
  };
  protoOf(IntRange).toString = function () {
    return '' + this.c3_1 + '..' + this.d3_1;
  };
  function IntProgressionIterator(first, last, step) {
    IntIterator.call(this);
    this.f3_1 = step;
    this.g3_1 = last;
    this.h3_1 = this.f3_1 > 0 ? first <= last : first >= last;
    this.i3_1 = this.h3_1 ? first : this.g3_1;
  }
  protoOf(IntProgressionIterator).c = function () {
    return this.h3_1;
  };
  protoOf(IntProgressionIterator).y2 = function () {
    var value = this.i3_1;
    if (value === this.g3_1) {
      if (!this.h3_1)
        throw NoSuchElementException_init_$Create$();
      this.h3_1 = false;
    } else {
      this.i3_1 = this.i3_1 + this.f3_1 | 0;
    }
    return value;
  };
  function Companion_4() {
  }
  var Companion_instance_4;
  function Companion_getInstance_4() {
    return Companion_instance_4;
  }
  function IntProgression(start, endInclusive, step) {
    if (step === 0)
      throw IllegalArgumentException_init_$Create$_0('Step must be non-zero.');
    if (step === -2147483648)
      throw IllegalArgumentException_init_$Create$_0('Step must be greater than Int.MIN_VALUE to avoid overflow on negation.');
    this.c3_1 = start;
    this.d3_1 = getProgressionLastElement(start, endInclusive, step);
    this.e3_1 = step;
  }
  protoOf(IntProgression).b = function () {
    return new IntProgressionIterator(this.c3_1, this.d3_1, this.e3_1);
  };
  protoOf(IntProgression).f = function () {
    return this.e3_1 > 0 ? this.c3_1 > this.d3_1 : this.c3_1 < this.d3_1;
  };
  protoOf(IntProgression).equals = function (other) {
    var tmp;
    if (other instanceof IntProgression) {
      tmp = this.f() && other.f() || (this.c3_1 === other.c3_1 && this.d3_1 === other.d3_1 && this.e3_1 === other.e3_1);
    } else {
      tmp = false;
    }
    return tmp;
  };
  protoOf(IntProgression).hashCode = function () {
    return this.f() ? -1 : imul(31, imul(31, this.c3_1) + this.d3_1 | 0) + this.e3_1 | 0;
  };
  protoOf(IntProgression).toString = function () {
    return this.e3_1 > 0 ? '' + this.c3_1 + '..' + this.d3_1 + ' step ' + this.e3_1 : '' + this.c3_1 + ' downTo ' + this.d3_1 + ' step ' + (-this.e3_1 | 0);
  };
  function appendElement(_this__u8e3s4, element, transform) {
    if (!(transform == null))
      _this__u8e3s4.a(transform(element));
    else {
      if (element == null ? true : isCharSequence(element))
        _this__u8e3s4.a(element);
      else {
        if (element instanceof Char)
          _this__u8e3s4.k2(element.j3_1);
        else {
          _this__u8e3s4.a(toString_1(element));
        }
      }
    }
  }
  //region block: post-declaration
  protoOf(AbstractList).asJsReadonlyArrayView = asJsReadonlyArrayView;
  protoOf(AbstractMutableList).asJsReadonlyArrayView = asJsReadonlyArrayView;
  protoOf(EmptyList).asJsReadonlyArrayView = asJsReadonlyArrayView;
  //endregion
  //region block: init
  Companion_instance = new Companion();
  Unit_instance = new Unit();
  Companion_instance_2 = new Companion_2();
  EmptyIterator_instance = new EmptyIterator();
  Companion_instance_4 = new Companion_4();
  //endregion
  //region block: exports
  function $jsExportAll$(_) {
    var $kotlin = _.kotlin || (_.kotlin = {});
    var $kotlin$collections = $kotlin.collections || ($kotlin.collections = {});
    defineProp($kotlin$collections, 'KtList', Companion_getInstance);
  }
  $jsExportAll$(_);
  _.$jsExportAll$ = $jsExportAll$;
  _.$_$ = _.$_$ || {};
  _.$_$.a = VOID;
  _.$_$.b = ArrayList_init_$Create$_0;
  _.$_$.c = ArrayList_init_$Create$;
  _.$_$.d = Unit_instance;
  _.$_$.e = addAll;
  _.$_$.f = checkIndexOverflow;
  _.$_$.g = collectionSizeOrDefault;
  _.$_$.h = emptyList;
  _.$_$.i = firstOrNull;
  _.$_$.j = equals;
  _.$_$.k = initMetadataForClass;
  _.$_$.l = protoOf;
  _.$_$.m = until;
  _.$_$.n = ensureNotNull;
  _.$_$.o = asList;
  //endregion
  return _;
}));

//# sourceMappingURL=kotlin-kotlin-stdlib.js.map
