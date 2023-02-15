package app.gym.config

import org.springframework.boot.test.context.TestConfiguration
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.junit.jupiter.Container
import java.io.File

@TestConfiguration
class TestContainersConfig {
    companion object {
        @JvmStatic
        private val container =
            DockerComposeContainer(File("./src/test/resources/docker-compose.yml"))
                .withExposedService("postgres", 5432)
                .apply { start() }
    }
}