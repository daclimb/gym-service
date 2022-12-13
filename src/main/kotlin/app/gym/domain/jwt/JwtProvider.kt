package app.gym.domain.jwt

import app.gym.domain.member.Member
import app.gym.domain.member.MemberRole
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.security.KeyPair
import java.util.*

class InvalidJwtTokenException : RuntimeException()

class JwtProvider(
    private val keyPair: KeyPair
) {
    fun generateJwtToken(member: Member): String {
        val millis = System.currentTimeMillis() + 1 * 24 * 60 * 60 * 1000

        return Jwts.builder()
            .setExpiration(Date(millis))
            .claim("memberId", member.id.toString())
            .claim("role", MemberRole.Member)
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

    fun parseMemberId(token: String): Long {
        return try {
            val claimsJwt = Jwts.parser()
                .setSigningKey(keyPair.public)
                .parseClaimsJws(token)

            val memberIdString: String = claimsJwt.body["memberId"] as String
            memberIdString.toLong()
        } catch (e: Exception) {
            throw InvalidJwtTokenException()
        }
    }
}