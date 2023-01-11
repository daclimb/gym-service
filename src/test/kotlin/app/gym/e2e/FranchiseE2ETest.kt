package app.gym.e2e

import app.gym.api.request.UpdateFranchiseRequest
import app.gym.api.response.AddFranchiseResponse
import app.gym.api.response.ClientErrorResponse
import app.gym.api.response.GetFranchiseResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPATestConfig
import app.gym.utils.TestDataGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
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
    classes = [AWSTestConfig::class, JPATestConfig::class, E2EAuthenticationConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FranchiseE2ETest(
) {
    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var authUtils: E2EAuthenticationConfig

    @BeforeAll
    fun beforeAll() {

    }

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

    @Test
    fun `Should return status code 200 when get franchise`() {
        val adminHeaders = authUtils.getHeadersWithCookieForAdmin()

        val request = TestDataGenerator.addFranchiseRequest()
        val addFranchiseResponse = template.exchange(
            "/api/franchise/",
            HttpMethod.POST,
            HttpEntity(request, adminHeaders),
            AddFranchiseResponse::class.java
        )
        val body = addFranchiseResponse.body
        assertNotNull(body)

        val franchiseId = body!!.franchiseId

        val memberHeaders = authUtils.getHeadersWithCookieForMember()

        val response = template.exchange(
            "/api/franchise/$franchiseId",
            HttpMethod.GET,
            HttpEntity(null, memberHeaders),
            GetFranchiseResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 400 when get not existing franchise`() {
        val memberHeaders = authUtils.getHeadersWithCookieForMember()

        val response = template.exchange(
            "/api/franchise/284572359837",
            HttpMethod.GET,
            HttpEntity(null, memberHeaders),
            ClientErrorResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when update franchise`() {
        val adminHeaders = authUtils.getHeadersWithCookieForAdmin()

        val addFranchiseRequest = TestDataGenerator.addFranchiseRequest()
        val addFranchiseResponse = template.exchange(
            "/api/franchise",
            HttpMethod.POST,
            HttpEntity(addFranchiseRequest, adminHeaders),
            AddFranchiseResponse::class.java
        )
        val body = addFranchiseResponse.body
        assertNotNull(body)

        val franchiseId = body!!.franchiseId

        val request = UpdateFranchiseRequest("name", "description")
        val response = template.exchange(
            "/api/franchise/$franchiseId",
            HttpMethod.PUT,
            HttpEntity(request, adminHeaders),
            Any::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }


}