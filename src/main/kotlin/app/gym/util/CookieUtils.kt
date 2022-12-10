package app.gym.util

import javax.servlet.http.Cookie


class CookieUtils {
    companion object {
        fun create(token: String): Cookie {
            val cookie = Cookie("jwt", token)
            cookie.maxAge = 1 * 24 * 60 * 60
            cookie.isHttpOnly = true
            return cookie
        }
    }

}