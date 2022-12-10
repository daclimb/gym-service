package app.user.domain.member

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = WithMockUserSecurityContextFactory::class)
annotation class WithMockUser(
    val userId: Long
)