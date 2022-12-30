package app.gym.e2e

import app.gym.api.request.LoginRequest
import app.gym.api.request.SignupRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.AddGymResponse
import app.gym.api.response.AddImageResponse
import app.gym.api.response.GetGymResponse
import app.gym.api.response.GetSimpleGymResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPAConfig
import app.gym.utils.TestDataGenerator
import com.amazonaws.services.s3.AmazonS3
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.http.*
import org.springframework.test.annotation.DirtiesContext.*
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.io.path.toPath

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AWSTestConfig::class, JPAConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GymE2ETest(
    @Value("\${admin.email}")
    val adminEmail: String,
    @Value("\${admin.password}")
    val adminPassword: String
) {

    @Value("\${cloud.aws.bucket}")
    private lateinit var bucket: String

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var s3client: AmazonS3

    private var memberEmail: String = ""
    private var memberPassword: String = "password"
    private var memberName: String = "member name"

    @BeforeAll
    fun beforeAll() {
        this.s3client.createBucket(bucket)
        val random = Random()
        memberEmail =
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString() + "_" + random.nextInt(1000) + "@email.com"
    }

    @AfterEach
    fun afterEach() {
//        SecurityContextHolder.getContext().authentication = null
    }

    @Test
    fun `Should return status code 201 when add gym`() {
        val cookies = getCookieForAdmin()
        val headers = HttpHeaders()
        cookies.forEach { headers.add("Cookie", it) }

        val request = TestDataGenerator.addGymRequest()
        val response =
            template.exchange("/api/gym", HttpMethod.POST, HttpEntity(request, headers), AddGymResponse::class.java)

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when delete gym`() {
        val cookies = getCookieForMember()
        val headers = HttpHeaders()
        cookies.forEach { headers.add("Cookie", it) }

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
        val cookies = getCookieForMember()
        val headers = HttpHeaders()
        cookies.forEach { headers.add("Cookie", it) }

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
                object : ParameterizedTypeReference<List<GetSimpleGymResponse>>() {})
        assertEquals(HttpStatus.OK, responses.statusCode)
    }

    @Test
    fun `Should return status code 200 and gym details when get gym`() {
        val cookies = getCookieForAdmin()
        val headers = HttpHeaders()
        cookies.forEach { headers.add("Cookie", it) }

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse =
            template.exchange("/api/gym", HttpMethod.POST, HttpEntity(addGymRequest, headers), AddGymResponse::class.java)

        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val getGymResponse: ResponseEntity<GetGymResponse> =
            template.getForEntity("/api/gym/$gymId", GetGymResponse::class.java)
        assertEquals(HttpStatus.OK, getGymResponse.statusCode)
    }

    @Test
    fun `Should return status code 200 when update gym`() {
        val cookies = getCookieForAdmin()
        val headers = HttpHeaders()
        cookies.forEach { headers.add("Cookie", it) }

        val addGymRequest = TestDataGenerator.addGymRequest()
        val addGymResponse = template.postForEntity("/api/gym", HttpEntity(addGymRequest, headers), AddGymResponse::class.java)

        assertNotNull(addGymResponse.body)
        val gymId = addGymResponse.body!!.gymId

        val request = UpdateGymRequest("name","franchise", "address", "description", emptyList(), 0.0, 0.0)

        val httpEntity = HttpEntity(request, headers)
        val response = template.exchange("/api/gym/$gymId", HttpMethod.PUT, httpEntity, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 201 and uuid when adding gym image`() {
        val cookies = getCookieForMember()
        val headers = HttpHeaders()
        cookies.forEach { headers.add("Cookie", it) }

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

    private fun getCookieForAdmin(): MutableList<String> {
        val loginRequest = LoginRequest(adminEmail, adminPassword)
        val loginResponse = template.postForEntity("/api/admin/login", loginRequest, Any::class.java)

        val cookies = loginResponse.headers["Set-Cookie"]
        assertNotNull(cookies)
        return cookies!!
    }

    private fun getCookieForMember(): MutableList<String> {
        val signupRequest = SignupRequest(memberEmail, memberPassword, memberName)
        val signupResponse = template.postForEntity("/api/member/signup", signupRequest, Any::class.java)

        val loginRequest = LoginRequest(adminEmail, adminPassword)
        val loginResponse = template.postForEntity("/api/admin/login", loginRequest, Any::class.java)

        val cookies = loginResponse.headers["Set-Cookie"]
        assertNotNull(cookies)
        return cookies!!
    }
}