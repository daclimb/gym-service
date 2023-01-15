package app.gym.domain.member

import app.gym.security.UserAuthentication
import app.gym.security.UserPrincipal
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithMockMemberSecurityContextFactory : WithSecurityContextFactory<WithMockMember> {
    override fun createSecurityContext(mockMember: WithMockMember): SecurityContext {
        val context = SecurityContextHolder.getContext()

        val principal = if (mockMember.userRole == UserRole.Admin) {
            UserPrincipal.admin()
        } else {
            UserPrincipal.member(mockMember.memberId)
        }
        context.authentication = UserAuthentication(principal)
        return context
    }
}
