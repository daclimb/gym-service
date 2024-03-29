package app.gym.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

@TestConfiguration
class TestContainersConfig {

    private val container = DockerComposeContainer(
        File("src/test/resources/docker-compose.yml")
    )

    @Bean
    fun psqlContainer(): DockerComposeContainer<*> {
        return container
    }
}