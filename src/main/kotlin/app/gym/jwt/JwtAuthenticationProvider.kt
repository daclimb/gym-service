package app.gym.jwt

import app.gym.domain.member.MemberPrincipal
import app.gym.domain.member.MemberRole
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import java.security.KeyPair


class InvalidJwtTokenException : RuntimeException()

class JwtAuthenticationProvider(
    private val keyPair: KeyPair
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {

        return try {
            val claimsJwt = Jwts.parser()
                .setSigningKey(keyPair.public)
                .parseClaimsJws((authentication as JwtAuthenticationToken).jsonWebToken)

            val memberRole = MemberRole.valueOf(claimsJwt.body["role"] as String)
            val memberPrincipal = if (memberRole == MemberRole.Admin) {
                null
            } else {
                val memberId = (claimsJwt.body["memberId"] as String).toLong()
                MemberPrincipal(memberId)
            }
            JwtAuthenticationToken(memberPrincipal, null, listOf(GrantedAuthority { memberRole.value }))

        } catch (e: RuntimeException) {
            throw InvalidJwtTokenException()
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}