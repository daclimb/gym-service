package app.gym.e2e

import app.gym.api.request.LoginRequest
import app.gym.api.request.SignupRequest
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@TestConfiguration
class E2EAuthenticationConfig(
    @Value("\${admin.email}")
    val adminEmail: String,
    @Value("\${admin.password}")
    val adminPassword: String,
    private val template: TestRestTemplate
) {
    private var memberEmail: String = ""
        get() {
            if (field == "") {
                val random = Random()
                field =
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                        .toString() + "_" + random.nextInt(1000) + "@email.com"
            }
            return field
        }

    private var memberPassword: String = "password"
    private var memberName: String = "member name"

    fun getHeadersWithCookieForAdmin(): HttpHeaders {
        val loginRequest = LoginRequest(adminEmail, adminPassword)
        val loginResponse = template.postForEntity("/api/admin/login", loginRequest, Any::class.java)

        val cookies = loginResponse.headers["Set-Cookie"]
        Assertions.assertNotNull(cookies)

        val headers = HttpHeaders()
        cookies!!.forEach { headers.add("Cookie", it) }
        return headers
    }

    fun getHeadersWithCookieForMember(): HttpHeaders {
        val signupRequest = SignupRequest(memberEmail, memberPassword, memberName)
        template.postForEntity("/api/member/signup", signupRequest, Any::class.java)

        val loginRequest = LoginRequest(memberEmail, memberPassword)
        val loginResponse = template.postForEntity("/api/member/login", loginRequest, Any::class.java)

        val cookies = loginResponse.headers["Set-Cookie"]
        Assertions.assertNotNull(cookies)

        val headers = HttpHeaders()
        cookies!!.forEach { headers.add("Cookie", it) }
        return headers
    }
}