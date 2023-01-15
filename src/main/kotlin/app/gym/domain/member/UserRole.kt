package app.gym.domain.member

import org.springframework.security.core.GrantedAuthority

enum class UserRole(
    var value: String
): GrantedAuthority {
    Admin("ROLE_ADMIN"),
    Manager("ROLE_MANAGER"),
    Member("ROLE_MEMBER"),
    Guest("ROLE_GUEST");

    override fun getAuthority(): String {
        return this.value
    }
}