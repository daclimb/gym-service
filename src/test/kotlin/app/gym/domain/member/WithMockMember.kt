package app.gym.domain.member

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory::class)
annotation class WithMockMember(
    val memberId: Long,
    val memberRole: MemberRole = MemberRole.Member
)