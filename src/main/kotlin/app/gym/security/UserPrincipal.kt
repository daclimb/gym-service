package app.gym.security

import app.gym.domain.member.UserRole

class UserPrincipal private constructor(val memberId: Long? = null) {

    val isAdmin: Boolean
        get() {
            return this.memberId == null // TODO
        }

    val authorities: List<UserRole>
        get() {
            return if (this.memberId == null) { // TODO
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