package app.gym.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

@TestConfiguration
class TestContainersConfig {

    private val dockerImageName = "test-image"
    private val databaseName = "test-database"
    private val username = "username"
    private val password = "password"

    private val container = DockerComposeContainer<Nothing>(
        File("src/test/resources/docker-compose.yml")
    )

    @Bean
    fun psqlContainer(): DockerComposeContainer<Nothing> {
        return container
    }
}