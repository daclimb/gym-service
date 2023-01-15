package app.gym.security

import org.springframework.security.authentication.AbstractAuthenticationToken

class UserAuthentication(
    private val userPrincipal: UserPrincipal
): AbstractAuthenticationToken(userPrincipal.authorities)  {

    init {
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any {
        return this.userPrincipal
    }
}