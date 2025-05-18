package io.voidlang.syntax.exception

class LexError(char: Char, line: Int, column: Int): Exception("Unexpected $char at $line:$column") {
}