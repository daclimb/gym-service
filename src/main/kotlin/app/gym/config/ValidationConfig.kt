package app.gym.config

import app.gym.domain.gym.GymDetailsValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ValidationConfig {

    @Bean
    fun gymDetailsValidator(): GymDetailsValidator {
        return GymDetailsValidator()
    }
}