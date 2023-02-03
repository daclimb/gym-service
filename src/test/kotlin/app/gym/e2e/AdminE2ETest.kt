package app.gym.e2e

import app.gym.api.request.LoginRequest
import app.gym.api.response.SimpleSuccessfulResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPATestConfig
import app.gym.config.TestContainersConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.testcontainers.containers.DockerComposeContainer

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AWSTestConfig::class, JPATestConfig::class, E2EAuthenticationConfig::class, TestContainersConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminE2ETest {

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var container: DockerComposeContainer<*>

    @Value("\${admin.email}")
    private lateinit var email: String

    @Value("\${admin.password}")
    private lateinit var password: String

    @Test
    fun `Should return status code 200 when login`() {
        val request = LoginRequest(email, password)

        val response = template.exchange(
            "/api/admin/login",
            HttpMethod.POST,
            HttpEntity(request, null),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 400 when login with invalid form`() {
        var request = LoginRequest("invalid@email.com", password)

        var response = template.exchange(
            "/api/admin/login",
            HttpMethod.POST,
            HttpEntity(request, null),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        request = LoginRequest(email, "invalid_password")

        response = template.exchange(
            "/api/admin/login",
            HttpMethod.POST,
            HttpEntity(request, null),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}