package app.gym.domain.member

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockMemberSecurityContextFactory::class)
annotation class WithCustomMockUser(
    val memberId: Long = 1L,
    val userRole: UserRole = UserRole.Member
)