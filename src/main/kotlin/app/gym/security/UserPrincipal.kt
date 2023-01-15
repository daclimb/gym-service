package app.gym.security

import app.gym.domain.member.UserRole
import org.springframework.security.authentication.AbstractAuthenticationToken

class UserPrincipal private constructor(val memberId: Long? = null) {

    val isAdmin: Boolean
        get() {
            return this.memberId == null
        }

    val authorities: List<UserRole>
        get() {
            return if (this.memberId == null) {
                listOf(UserRole.Admin)
            } else {
                listOf(UserRole.Member)
            }
        }

    companion object {
        fun member(memberId: Long): UserPrincipal {
            return UserPrincipal(memberId)
        }

        fun admin(): UserPrincipal {
            return UserPrincipal()
        }
    }
}