package app.gym.security

import app.gym.domain.member.Member
import app.gym.domain.member.UserRole
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.util.*

const val TOKEN_EXPIRATION_MILLIS: Long = 24 * 60 * 60 * 1000
const val JWT_CLAIM_ROLE = "role"
const val JWT_CLAIM_MEMBER_ID = "memberId"

@Component
class JwtTokenHandler(
    private val keyPair: KeyPair,
) {
    fun generateJwtToken(member: Member): String {
        return Jwts.builder()
            .setExpiration(Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MILLIS))
            .claim(JWT_CLAIM_MEMBER_ID, member.id.toString())
            .claim(JWT_CLAIM_ROLE, member.role)
            .signWith(SignatureAlgorithm.RS256, keyPair.private)
            .compact()
    }

    fun generateAdminJwtToken(): String {
        return Jwts.builder()
            .setExpiration(Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MILLIS))
            .claim(JWT_CLAIM_ROLE, UserRole.Admin)
            .signWith(SignatureAlgorithm.RS256, keyPair.private)
            .compact()
    }

    fun createPrincipal(token: String): UserPrincipal {
        try {
            val claimsJwt = Jwts.parser()
                .setSigningKey(keyPair.public)
                .parseClaimsJws(token)

            return when (val userRole = UserRole.valueOf(claimsJwt.body["role"] as String)) {
                UserRole.Admin -> UserPrincipal.admin()
                UserRole.Member -> {
                    val memberId = (claimsJwt.body["memberId"] as String).toLong()
                    UserPrincipal.member(memberId)
                }
                else -> throw Error("Unsupported user role - $userRole")
            }

        } catch (e: RuntimeException) {
            throw InvalidJwtTokenException(e)
        }
    }

}