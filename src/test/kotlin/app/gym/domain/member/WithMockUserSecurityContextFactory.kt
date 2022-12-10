package app.gym.domain.member

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockUserSecurityContextFactory: WithSecurityContextFactory<WithMockUser> {
    override fun createSecurityContext(mockUser: WithMockUser): SecurityContext {
        val context = SecurityContextHolder.getContext()

        val memberPrincipal = MemberPrincipal(mockUser.userId)
        val authenticationToken = UsernamePasswordAuthenticationToken(memberPrincipal, null)
        context.authentication = authenticationToken

        return context
    }
}
