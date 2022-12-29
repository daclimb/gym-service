package app.gym.domain.member

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockMemberSecurityContextFactory::class)
annotation class WithMockMember(
    val memberId: Long = 0L,
    val memberRole: MemberRole = MemberRole.Member
)