package app.gym.e2e

import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.AddGymResponse
import app.gym.api.response.AddImageResponse
import app.gym.api.response.GetGymListResponse
import app.gym.api.response.GetGymResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPATestConfig
import app.gym.config.TestContainersConfig
import app.gym.util.TestDataGenerator
import com.amazonaws.services.s3.AmazonS3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.util.*
import kotlin.io.path.toPath

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AWSTestConfig::class, JPATestConfig::class, E2EAuthenticationConfig::class, TestContainersConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GymE2ETest {

    @Value("\${cloud.aws.bucket}")
    private lateinit var bucket: String

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var s3client: AmazonS3

    @Autowired
    private lateinit var authUtils: E2EAuthenticationConfig

    @BeforeAll
    fun beforeAll() {
        this.s3client.createBucket(bucket)
    }

    @Test
    fun `Should return status code 201 when add gym`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()
        val request = TestDataGenerator.addGymRequest()
        val response =
            template.exchange("/api/gym", HttpMethod.POST, HttpEntity(request, headers), AddGymResponse::class.java)

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when delete gym`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse = template.exchange(
            "/api/gym",
            HttpMethod.POST,
            HttpEntity(addGymRequest, headers),
            AddGymResponse::class.java
        )
        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val response =
            template.exchange("/api/gym/$gymId", HttpMethod.DELETE, HttpEntity(null, headers), Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }


    @Test
    fun `Should return status code 400 when delete not existing gym`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val response =
            template.exchange("/api/gym/93485713495", HttpMethod.DELETE, HttpEntity(null, headers), Any::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return status code 200 and simple gym list when get simple gym list`() {
        val responses =
            template.exchange(
                "/api/gym",
                HttpMethod.GET,
                null,
                GetGymListResponse::class.java
            )
        assertEquals(HttpStatus.OK, responses.statusCode)
    }

    @Test
    fun `Should return status code 200 and gym when get gym`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse =
            template.exchange(
                "/api/gym",
                HttpMethod.POST,
                HttpEntity(addGymRequest, headers),
                AddGymResponse::class.java
            )
        assertNotNull(addGymResponse.body)

        val gymId = addGymResponse.body!!.gymId

        val getGymResponse: ResponseEntity<GetGymResponse> =
            template.getForEntity("/api/gym/$gymId", GetGymResponse::class.java)
        assertEquals(HttpStatus.OK, getGymResponse.statusCode)
    }

    @Test
    fun `Should return status code 200 when update gym`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse =
            template.postForEntity("/api/gym", HttpEntity(addGymRequest, headers), AddGymResponse::class.java)

        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val request = UpdateGymRequest("name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList(), "")

        val httpEntity = HttpEntity(request, headers)
        val response = template.exchange("/api/gym/$gymId", HttpMethod.PUT, httpEntity, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when update gym details`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse =
            template.postForEntity("/api/gym", HttpEntity(addGymRequest, headers), AddGymResponse::class.java)

        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val phoneNumber = "010-1111-1111"
        val details = "{\"phoneNumber\":\"$phoneNumber\"}"
        val request =
            UpdateGymRequest("name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList(), details)

        val httpEntity = HttpEntity(request, headers)
        val response = template.exchange("/api/gym/$gymId", HttpMethod.PUT, httpEntity, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)

        val getGymResponse = template.getForEntity("/api/gym/$gymId", GetGymResponse::class.java)
        assertEquals(phoneNumber, getGymResponse.body!!.details.phoneNumber)
    }

    @Test
    fun `Should return status code 400 when update gym details with invalid name`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse =
            template.postForEntity("/api/gym", HttpEntity(addGymRequest, headers), AddGymResponse::class.java)

        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val name = "invalidName"
        val value = "value"
        val details = "{\"$name\":\"$value\"}"
        val request =
            UpdateGymRequest("name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList(), details)

        val httpEntity = HttpEntity(request, headers)
        val response = template.exchange("/api/gym/$gymId", HttpMethod.PUT, httpEntity, Any::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return status code 400 when update gym details with invalid value`() {
        val headers = authUtils.getHeadersWithCookieForAdmin()

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse =
            template.postForEntity("/api/gym", HttpEntity(addGymRequest, headers), AddGymResponse::class.java)

        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val name = "phoneNumber"
        val value = 1234
        val details = "{\"$name\":$value}"
        val request =
            UpdateGymRequest("name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList(), details)

        val httpEntity = HttpEntity(request, headers)
        val response = template.exchange("/api/gym/$gymId", HttpMethod.PUT, httpEntity, Any::class.java)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return status code 201 and uuid when adding gym image`() {
        val headers = authUtils.getHeadersWithCookieForMember()

        val imageResource = ClassPathResource("images/pooh.png")
        val file = FileSystemResource(imageResource.uri.toPath())

        val body = LinkedMultiValueMap<String, Any>()
        body.add("image", file)

        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val httpEntity = HttpEntity<MultiValueMap<String, Any>>(body, headers)
        val response = template.postForEntity("/api/gym/image", httpEntity, AddImageResponse::class.java)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)

        val uuid = response.body!!.id
        assertNotNull(uuid)
    }
}