package app.gym.e2e

import app.gym.api.response.AddFranchiseResponse
import app.gym.api.response.GetFranchiseResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPATestConfig
import app.gym.utils.AuthenticationUtils
import app.gym.utils.TestDataGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.*

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AWSTestConfig::class, JPATestConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FranchiseE2ETest(
) {
    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var authUtils: AuthenticationUtils

    @Test
    fun `Should return status code 201 when add franchise`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val request = TestDataGenerator.addFranchiseRequest()
        val response = template.exchange(
            "/api/franchise/",
            HttpMethod.POST,
            HttpEntity(request, headers),
            AddFranchiseResponse::class.java
        )

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }
//
//    @Test
//    fun `Should return status code 400 when `

    @Test
    fun `Should return status code 200 when get franchise`() {
        var headers = authUtils.getHeadersWithCookieForAdmin()

        val request = TestDataGenerator.addFranchiseRequest()
        val addFranchiseResponse = template.exchange(
            "/api/franchise/",
            HttpMethod.POST,
            HttpEntity(request, headers),
            AddFranchiseResponse::class.java
        )
        val franchiseId = addFranchiseResponse.body!!.franchiseId

        headers = authUtils.getHeadersWithCookieForMember()

        val response = template.exchange(
            "/api/franchise/$franchiseId",
            HttpMethod.GET,
            HttpEntity(null, headers),
            GetFranchiseResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}