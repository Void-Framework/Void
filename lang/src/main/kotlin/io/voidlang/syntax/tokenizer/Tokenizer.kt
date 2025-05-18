package io.voidlang.syntax.tokenizer

import io.voidlang.syntax.Tokens
import io.voidlang.syntax.exception.LexError

data class Token(
    val type: Tokens,
    val lexeme: String,
    val line: Int,
    val column: Int
)

class Tokenizer(private val source: String) {

    private var current = 0
    private var line = 1
    private var column = 1
    private val tokens = mutableListOf<Token>()
    private val errors = mutableListOf<LexError>()

    private val keywords = mapOf(
        "fun" to Tokens.FUN,
        "val" to Tokens.VAL,
        "var" to Tokens.VAR,
        "class" to Tokens.CLASS,
        "if" to Tokens.IF,
        "else" to Tokens.ELSE,
        "when" to Tokens.WHEN,
        "for" to Tokens.FOR,
        "while" to Tokens.WHILE,
        "break" to Tokens.BREAK,
        "continue" to Tokens.CONTINUE,
        "return" to Tokens.RETURN,
        "throw" to Tokens.THROW,
        "try" to Tokens.TRY,
        "catch" to Tokens.CATCH,
        "finally" to Tokens.FINALLY,
        "this" to Tokens.THIS,
        "super" to Tokens.SUPER,
        "import" to Tokens.IMPORT,
        "export" to Tokens.EXPORT,
        "log" to Tokens.LOG,
        "error" to Tokens.ERROR,
        "warn" to Tokens.WARN,
        "as" to Tokens.AS,
        "fetch" to Tokens.FETCH,
        "true" to Tokens.BOOLEAN,
        "false" to Tokens.BOOLEAN,
        "null" to Tokens.NULL,
        "undefined" to Tokens.UNDEFINED,
        "object" to Tokens.OBJECT
    )

    private val annotations = mapOf(
        "Suspend" to Tokens.ANNOTATION_NAME,
        "HeavyRequest" to Tokens.ANNOTATION_NAME
    )

    private val doubleOperations = mapOf(
        '=' to Tokens.EQUALSEQUALS,
        '|' to Tokens.OROR,
        '&' to Tokens.ANDAND,
        '+' to Tokens.INCREMENT,
        '-' to Tokens.DECREMENT
    )
    private val mathOperations = mapOf(
        '=' to Tokens.EQUALS,
        '+' to Tokens.PLUS,
        '-' to Tokens.MINUS,
        '*' to Tokens.STAR,
        '/' to Tokens.SLASH,
        '%' to Tokens.PERCENT,
        '>' to Tokens.GREATER,
        '<' to Tokens.LESS,
        '.' to Tokens.DOT,
        '!' to Tokens.NOT,
        '?' to Tokens.QUESTIONMARK
    )

    private val operations = mapOf(
        "!=" to Tokens.NOT_EQUALS,
        "/>" to Tokens.END_TAG,
        "..." to Tokens.SPREAD,
        ">=" to Tokens.GTE,
        "<=" to Tokens.LTE,
        "+=" to Tokens.PLUSEQ,
        "-=" to Tokens.MINUSEQ,
        "*=" to Tokens.STAREQ,
        "/=" to Tokens.SLASHEQ,
        "%=" to Tokens.PERCENTEQ,
        "->" to Tokens.ARROW,
        "!!" to Tokens.NULL_ASSERT,
        "?." to Tokens.SAFE_CALL,
        "?:" to Tokens.ELVIS
    )

    fun lex(): List<Token> {
        while (!isAtEnd()) {
            val start = current
            val char = advance()
            handleNextChar(char, start)
        }
        errors.forEach {
            throw it
        }
        return tokens
    }

    private fun handleNextChar(char: Char, start: Int, html: Boolean = false) {
        when {
            char.isWhitespace() -> {
                if (char == '\n') {
                    line++
                    column = 1
                } else {
                    column++
                }
            }
            char.isDigit() -> lexNumber(start)
            char.isLetter() -> lexIdentifier(start)
            else -> {
                if (html) errors.add(LexError(char, line, column))
                handleSymbol(char, start)
            }
        }
    }

    private fun handleSymbol(char: Char, start: Int) {
        when (char) {
            '(' -> addToken(Tokens.LPAREN, "(", start)
            ')' -> addToken(Tokens.RPAREN, ")", start)
            '{' -> addToken(Tokens.LBRACE, "{", start)
            '}' -> addToken(Tokens.RBRACE, "}", start)
            ',' -> addToken(Tokens.COMMA, ",", start)
            ':' -> addToken(Tokens.COLON, ":", start)
            '$' -> addToken(Tokens.DOLLAR, "$", start)
            '@' -> addToken(Tokens.AT_SIGN, "@", start)
            ';' -> addToken(Tokens.SEMICOLON, ";", start)
            '[' -> addToken(Tokens.LBRACKET, "[", start)
            ']' -> addToken(Tokens.RBRACKET, "]", start)
            else -> handleOperations(start, char)
        }
    }


    private fun addToken(type: Tokens, lexeme: String, start: Int) {
        tokens.add(Token(type, lexeme, line, column + (start - current)))
    }

    private fun handleOperations(start: Int, char: Char): Boolean {
        if (doubleOperations.contains(char)) {
            if (peekNext() == char) {
                addToken(doubleOperations[char]!!, "$char$char", start)
                return true
            } else {
                return handleTwoCharOperations(start, char)
            }
        } else {
            return handleTwoCharOperations(start, char)
        }
    }
    private fun handleTwoCharOperations(start: Int, char: Char): Boolean {
        if (char == '.' && peekNext() == '.' && peekNextNext() == '.') {
            addToken(Tokens.SPREAD, "...", start)
            return true
        } else {
            if (operations.contains("$char${peekNext()}")) {
                addToken(operations["$char${peekNext()}"]!!, "$char${peekNext()}", start)
                return true
            } else {
                if (mathOperations.contains(char)) {
                    addToken(mathOperations[char]!!, "$char", start)
                    return true
                } else {
                    errors.add(LexError(char, line, column))
                    return false
                }
            }
        }
    }

    private fun lexIdentifier(start: Int) {
        while (!isAtEnd() && peek().isLetterOrDigit()) advance()
        val text = source.substring(start, current)
        val type = keywords[text] ?: Tokens.IDENTIFIER
        addToken(type, text, start)
    }

    private fun lexNumber(start: Int) {
        while (!isAtEnd() && peek().isDigit()) advance()
        val number = source.substring(start, current)
        addToken(Tokens.NUMBER, number, start)
    }

    private fun advance(): Char {
        val char = source[current]
        current++
        column++
        return char
    }
    private fun peek() = if (!isAtEnd()) source[current] else '\u0000'
    private fun peekNext(): Char {
        return try {
            source[current + 1]
        } catch (_: Exception) {
            //IndexOutOfBounds
            '\u9999'
        }
    }
    private fun peekNextNext(): Char {
        return try {
            source[current + 2]
        } catch (_: Exception) {
            //IndexOutOfBounds
            '\u9999'
        }
    }
    private fun isAtEnd() = current >= source.length
}