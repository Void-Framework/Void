package io.voidx.dto

import io.voidx.page.Path

/**
 * HTTP cookie builder type used by ResponseDTO helpers.
 *
 * This class is intended to be created via the [cookie] DSL helper and then
 * configured with a name, value and optional attributes like [path], [maxAge],
 * [httpOnly], [secure] and [sameSite].
 *
 * Example:
 * - cookie { name = "sid"; value = token; path = "/"; httpOnly = true; sameSite = SameSite.STRICT }
 */
@ConsistentCopyVisibility
data class Cookie internal constructor(
    val nothing: Unit,
) {
    /** Cookie name (required). */
    lateinit var name: String

    /** Cookie value (required). */
    lateinit var value: String

    /** Path scope for the cookie (e.g. "/"). */
    lateinit var path: Path

    /** Max age in seconds; when null, the cookie is a session cookie. */
    var maxAge: Long? = null

    /** When true, the cookie is not accessible via client-side scripts. */
    var httpOnly: Boolean = false

    /** When true, the cookie is only sent over HTTPS. */
    var secure: Boolean = false

    /** SameSite attribute controlling cross-site transmission. */
    lateinit var sameSite: SameSite

    constructor(name: String, value: String, path: Path, maxAge: Long?, httpOnly: Boolean, secure: Boolean, sameSite: SameSite) : this(
        Unit,
    ) {
        this.name = name
        this.value = value
        this.path = path
        this.maxAge = maxAge
        this.httpOnly = httpOnly
        this.secure = secure
        this.sameSite = sameSite
    }

    /**
     * Serializes this cookie into a Set-Cookie header value.
     */
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

/** SameSite policy for cookies. */
enum class SameSite { LAX, STRICT, NONE }

/**
 * DSL helper to create and configure a [Cookie].
 */
fun cookie(builder: Cookie.() -> Unit): Cookie {
    val cookie = Cookie(Unit)
    cookie.builder()
    return cookie
}
