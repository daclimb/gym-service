package app.gym.domain.member

import app.gym.config.JwtAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockUserSecurityContextFactory : WithSecurityContextFactory<WithMockMember> {
    override fun createSecurityContext(mockMember: WithMockMember): SecurityContext {
        val context = SecurityContextHolder.getContext()

        val memberPrincipal = MemberPrincipal(mockMember.memberId)
        val jwtAuthenticationToken = JwtAuthenticationToken(
            memberPrincipal, null,
            listOf(GrantedAuthority { mockMember.memberRole.value })
        )

        context.authentication = jwtAuthenticationToken

        return context
    }
}
