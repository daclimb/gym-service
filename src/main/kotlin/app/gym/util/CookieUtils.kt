package app.gym.util

import org.springframework.http.ResponseCookie

class CookieUtils {
    companion object {
        fun createCookie(
            token: String,
            maxAge: Long = 1 * 24 * 60 * 60,
            isHttpOnly: Boolean = true,
        ): ResponseCookie {
            val cookieBuilder = ResponseCookie.from("jwt", token)
            cookieBuilder.maxAge(maxAge)
            cookieBuilder.httpOnly(isHttpOnly)
            return cookieBuilder.build()
        }
    }
}