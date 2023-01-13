package app.gym.domain.member

import app.gym.jwt.JwtAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockMemberSecurityContextFactory : WithSecurityContextFactory<WithMockMember> {
    override fun createSecurityContext(mockMember: WithMockMember): SecurityContext {
        val context = SecurityContextHolder.getContext()


        val memberPrincipal = if (mockMember.memberRole == MemberRole.Admin) {
            null
        } else {
            MemberPrincipal(mockMember.memberId)
        }

        val jwtAuthenticationToken = JwtAuthenticationToken(
            memberPrincipal, null,
            listOf(GrantedAuthority { mockMember.memberRole.value })
        )

        context.authentication = jwtAuthenticationToken

        return context
    }
}
