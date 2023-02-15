package app.gym.e2e

import app.gym.api.request.AddTagRequest
import app.gym.api.response.GetTagListResponse
import app.gym.api.response.SimpleSuccessfulResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPATestConfig
import app.gym.config.TestContainersConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AWSTestConfig::class, JPATestConfig::class, E2EAuthenticationConfig::class, TestContainersConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagE2ETest {

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var authUtils: E2EAuthenticationConfig

    @Test
    fun `Should return status code 201 when add tag`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val request = AddTagRequest("new tag")
        val response = template.exchange(
            "/api/tag",
            HttpMethod.POST,
            HttpEntity(request, headers),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `Should return status code 400 when add duplicated tag`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()
        val request = AddTagRequest("new tag")

        template.exchange(
            "/api/tag",
            HttpMethod.POST,
            HttpEntity(request, headers),
            SimpleSuccessfulResponse::class.java
        )

        val response = template.exchange(
            "/api/tag",
            HttpMethod.POST,
            HttpEntity(request, headers),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when get tag list`() {
        val response = template.exchange(
            "/api/tag",
            HttpMethod.GET,
            null,
            GetTagListResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when delete tag`() {

        val headers = authUtils.getHeadersWithCookieForAdmin()
        val request = AddTagRequest("new tag")

        template.exchange(
            "/api/tag",
            HttpMethod.POST,
            HttpEntity(request, headers),
            SimpleSuccessfulResponse::class.java
        )

        val getTagListResponse = template.exchange(
            "/api/tag",
            HttpMethod.GET,
            HttpEntity(null, headers),
            GetTagListResponse::class.java
        )

        val tagId = getTagListResponse.body!!.tags[0].id

        val response = template.exchange(
            "/api/tag/$tagId",
            HttpMethod.DELETE,
            HttpEntity(null, headers),
            SimpleSuccessfulResponse::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
    }
}