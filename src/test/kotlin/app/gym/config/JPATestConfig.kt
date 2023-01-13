package app.gym.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@TestConfiguration
class JPATestConfig {
}