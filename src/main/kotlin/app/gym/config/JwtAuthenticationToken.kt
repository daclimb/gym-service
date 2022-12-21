package app.gym.config

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken : AbstractAuthenticationToken {
    var jsonWebToken: String? = null; private set
    private var principal: Any? = null
    private var credentials: Any? = null

    constructor(jsonWebToken: String?) : super(null) {
        this.jsonWebToken = jsonWebToken
        this.isAuthenticated = false
    }

    constructor(principal: Any?, credentials: Any?, authorities: Collection<GrantedAuthority>?) : super(authorities) {
        this.principal = principal
        this.credentials = credentials
        super.setAuthenticated(true)
    }

    override fun getCredentials(): Any? {
        return credentials
    }

    override fun getPrincipal(): Any? {
        return principal
    }
}