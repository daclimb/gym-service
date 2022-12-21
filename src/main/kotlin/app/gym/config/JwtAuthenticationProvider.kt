package app.gym.config

import app.gym.domain.member.MemberPrincipal
import app.gym.domain.member.MemberRole
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Component
import java.security.KeyPair

@Component
class JwtAuthenticationProvider(
    private val keyPair: KeyPair
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication): Authentication {

        return try {
            val claimsJwt = Jwts.parser()
                .setSigningKey(keyPair.public)
                .parseClaimsJws((authentication as JwtAuthenticationToken).jsonWebToken)

            val memberId = (claimsJwt.body["memberId"] as String).toLong()
            val memberPrincipal = MemberPrincipal(memberId)
            val memberRole = MemberRole.valueOf(claimsJwt.body["role"] as String)
            JwtAuthenticationToken(memberPrincipal, null, listOf(GrantedAuthority {  memberRole.value }))

        } catch (e: Exception) {
            throw InvalidJwtTokenException()
        }
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}