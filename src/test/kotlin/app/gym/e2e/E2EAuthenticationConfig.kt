package app.gym.e2e

import app.gym.util.CookieUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.http.HttpHeaders
import java.security.KeyPair
import java.util.*

const val TOKEN_EXPIRATION_MILLIS: Long = 10000L * 24 * 60 * 60 * 1000

@TestConfiguration
class E2EAuthenticationConfig(
    @Value("\${jwt.token.admin}")
    private val adminJwtToken: String,
    @Value("\${jwt.token.member}")
    private val memberJwtToken: String,
    private val keyPair: KeyPair,

    ) {
    fun getHeadersWithCookieForAdmin(): HttpHeaders {
//        val token = Jwts.builder()
//            .setExpiration(Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MILLIS))
//            .claim(JWT_CLAIM_ROLE, UserRole.Admin)
//            .signWith(SignatureAlgorithm.RS256, keyPair.private)
//            .compact()
        val cookie = CookieUtils.createCookie(adminJwtToken, (TOKEN_EXPIRATION_MILLIS / 1000)).toString()
        val header = HttpHeaders()
        header.add("Cookie", cookie)
        return header
    }

    fun getHeadersWithCookieForMember(): HttpHeaders {
        val cookie = CookieUtils.createCookie(memberJwtToken, (TOKEN_EXPIRATION_MILLIS / 1000)).toString()
        val header = HttpHeaders()
        header.add("Cookie", cookie)
        return header
    }
}