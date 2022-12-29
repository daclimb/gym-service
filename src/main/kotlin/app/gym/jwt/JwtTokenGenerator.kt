package app.gym.jwt

import app.gym.domain.member.Member
import app.gym.domain.member.MemberRole
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.util.*

@Component
class JwtTokenGenerator(
    private val keyPair: KeyPair
) {
    fun generateJwtToken(member: Member): String {
        val millis = System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000

        return Jwts.builder()
            .setExpiration(Date(millis))
            .claim("memberId", member.id.toString())
            .claim("role", member.role)
            .signWith(SignatureAlgorithm.RS256, keyPair.private)
            .compact()
    }

    fun generateAdminJwtToken(): String {
        val millis = System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000

        return Jwts.builder()
            .setExpiration(Date(millis))
            .claim("role", MemberRole.Admin)
            .signWith(SignatureAlgorithm.RS256, keyPair.private)
            .compact()
    }

}