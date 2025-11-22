package io.voidx.dto

import io.voidx.page.Path

@ConsistentCopyVisibility
data class Cookies internal constructor(
    val nothing: Unit
) {
    lateinit var name: String
    lateinit var value: String
    lateinit var path: Path
    var maxAge: Long? = null
    var httpOnly: Boolean = false
    var secure: Boolean = false
    lateinit var sameSite: SameSite

    constructor(name: String, value: String, path: Path, maxAge: Long?, httpOnly: Boolean, secure: Boolean, sameSite: SameSite) : this(
        Unit
    ) {
        this.name = name
        this.value = value
        this.path = path
        this.maxAge = maxAge
        this.httpOnly = httpOnly
        this.secure = secure
        this.sameSite = sameSite
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("$name=$value")
        sb.append("; Path=$path")
        if (maxAge != null) sb.append("; Max-Age=$maxAge")
        if (httpOnly) sb.append("; HttpOnly")
        if (secure) sb.append("; Secure")
        sb.append("; SameSite=${sameSite.name}")
        return sb.toString()
    }
}

enum class SameSite { LAX, STRICT, NONE }

fun cookie(builder: Cookies.() -> Unit): Cookies {
    val cookie = Cookies(Unit)
    cookie.builder()
    return cookie
}