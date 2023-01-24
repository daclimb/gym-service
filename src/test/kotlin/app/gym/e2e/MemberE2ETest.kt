package app.gym.e2e

import app.gym.api.request.LoginRequest
import app.gym.api.request.SignupRequest
import app.gym.api.response.ClientErrorResponse
import app.gym.api.response.MeResponse
import app.gym.api.response.SimpleSuccessfulResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPATestConfig
import app.gym.config.TestContainersConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.testcontainers.containers.DockerComposeContainer
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.stream.Stream

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AWSTestConfig::class, JPATestConfig::class, E2EAuthenticationConfig::class, TestContainersConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberE2ETest {

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var authUtils: E2EAuthenticationConfig

    @Autowired
    private lateinit var container: DockerComposeContainer<*>

    private val random: Random = Random()

    @Test
    fun `Should return status code 201 when signup`() {
        val validEmail = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            .toString() + "_" + random.nextInt(1000) + "@email.com"
        val request = SignupRequest(validEmail, "password", "name")

        val response = template.exchange(
            "/api/member/signup",
            HttpMethod.POST,
            HttpEntity(request, null),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @ParameterizedTest
    @MethodSource("signupFormProvider")
    fun `Should return status code 400 when sign up with invalid parameters`(
        email: String?,
        password: String?,
        name: String?,
        expectedStatusCode: HttpStatus,
    ) {
        val request = mapOf(
            "email" to email,
            "password" to password,
            "name" to name
        )

        val response = template.exchange(
            "/api/member/signup",
            HttpMethod.POST,
            HttpEntity(request, null),
            ClientErrorResponse::class.java
        )

        assertEquals(expectedStatusCode, response.statusCode)
    }

    private fun signupFormProvider(): Stream<Arguments> {
        val validEmail = "valid@email.com"
        val validPassword = "valid_password"
        val validName = "valid_name"
        return Stream.of(
            Arguments.of(null, validPassword, validName, HttpStatus.BAD_REQUEST),
            Arguments.of("invalid_email", validPassword, validName, HttpStatus.BAD_REQUEST),

            Arguments.of(validEmail, null, validName, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, "1", validName, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, "123", validName, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, "123123123123123123123123", validName, HttpStatus.BAD_REQUEST),

            Arguments.of(validEmail, validPassword, null, HttpStatus.BAD_REQUEST),
            Arguments.of(
                validEmail,
                validPassword,
                "this name is toooooooooooooooooooooooooooooo long to create member...",
                HttpStatus.BAD_REQUEST
            )
        )
    }

    @Test
    fun `Should return status code 200 when login`() {
        val validEmail = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            .toString() + "_" + random.nextInt(1000) + "@email.com"
        val password = "password"

        val signupRequest = SignupRequest(validEmail, password, "name")
        template.exchange(
            "/api/member/signup",
            HttpMethod.POST,
            HttpEntity(signupRequest, null),
            ClientErrorResponse::class.java
        )

        val request = LoginRequest(validEmail, password)

        val response = template.exchange(
            "/api/member/login",
            HttpMethod.POST,
            HttpEntity(request, null),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 400 when login with not existing email`() {
        val validEmail = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            .toString() + "_" + random.nextInt(1000) + "@email.com"
        val password = "password"

        val signupRequest = SignupRequest(validEmail, password, "name")
        template.exchange(
            "/api/member/signup",
            HttpMethod.POST,
            HttpEntity(signupRequest, null),
            ClientErrorResponse::class.java
        )

        val request = LoginRequest("invalid@email.com", password)

        val response = template.exchange(
            "/api/member/login",
            HttpMethod.POST,
            HttpEntity(request, null),
            ClientErrorResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Failure: member not found", response.body!!.message)
    }

    @Test
    fun `Should return status code 400 when login with not matched password`() {
        val validEmail = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            .toString() + "_" + random.nextInt(1000) + "@email.com"
        val password = "password"

        val signupRequest = SignupRequest(validEmail, password, "name")
        template.exchange(
            "/api/member/signup",
            HttpMethod.POST,
            HttpEntity(signupRequest, null),
            ClientErrorResponse::class.java
        )

        val request = LoginRequest(validEmail, "invalid_password")
        val response = template.exchange(
            "/api/member/login",
            HttpMethod.POST,
            HttpEntity(request, null),
            ClientErrorResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Failure: password not matched", response.body!!.message)
    }

    @Test
    fun `Should return status code 200 when get me`() {
        val headers = authUtils.getHeadersWithCookieForMember()

        val response = template.exchange(
            "/api/member/me",
            HttpMethod.GET,
            HttpEntity(null, headers),
            MeResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}
