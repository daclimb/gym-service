package app.user.config

import mu.KLogger
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggingConfig {

    @Bean
    fun KLogger(): KLogger {
        return KotlinLogging.logger {}
    }
}