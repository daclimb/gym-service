package app.gym.e2e

import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.AddGymResponse
import app.gym.api.response.AddImageResponse
import app.gym.api.response.GetGymResponse
import app.gym.api.response.GetSimpleGymResponse
import app.gym.config.AWSTestConfig
import app.gym.config.JPAConfig
import app.gym.utils.TestDataGenerator
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
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.core.ParameterizedTypeReference
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
    classes = [AWSTestConfig::class, JPAConfig::class]
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GymE2ETest {

    @Value("\${cloud.aws.bucket}")
    private lateinit var bucket: String

    @Autowired
    private lateinit var template: TestRestTemplate

    @Autowired
    private lateinit var s3client: AmazonS3

    @BeforeAll
    fun beforeAll() {
        this.s3client.createBucket(bucket)
    }

    @Test
    fun `Should return status code 201 when add product`() {
        val request = TestDataGenerator.addGymRequest()
        val response = template.postForEntity("/api", request, AddGymResponse::class.java)

        assertEquals(HttpStatus.CREATED, response.statusCode)
    }

    @Test
    fun `Should return status code 200 when delete product`() {
        val addProductRequest = TestDataGenerator.addGymRequest()
        template.postForEntity("/api", addProductRequest, AddGymResponse::class.java)

        val getSimpleGymResponses: ResponseEntity<Array<GetSimpleGymResponse>> =
            template.getForEntity("/api", Array<GetSimpleGymResponse>::class)

        val id = getSimpleGymResponses.body!![0].id

        val response = template.exchange("/api/$id", HttpMethod.DELETE, null, Any::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Should return status code 400 when delete not existing product`() {
        val response = template.exchange("/api/93485713495", HttpMethod.DELETE, null, Any::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Should return status code 200 and simple products list when get simple products`() {
        val addProductRequest = TestDataGenerator.addGymRequest()
        template.postForEntity("/api", addProductRequest, AddGymResponse::class.java)

        val responses =
            template.exchange(
                "/api",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<GetSimpleGymResponse>>() {})
        assertEquals(HttpStatus.OK, responses.statusCode)
    }

    @Test
    fun `Should return status code 200 and product details when get product`() {
        val addProductRequest = TestDataGenerator.addGymRequest()
        template.postForEntity("/api", addProductRequest, AddGymResponse::class.java)

        val getSimpleGymResponses =
            template.exchange(
                "/api",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<GetSimpleGymResponse>>() {})
        val id = getSimpleGymResponses.body!![0].id

        val getGymResponse: ResponseEntity<GetGymResponse> =
            template.getForEntity("/api/$id", GetGymResponse::class.java)
        assertEquals(HttpStatus.OK, getGymResponse.statusCode)
    }

    @Test
    fun `Should return status code 200 when update product`() {
        val addProductRequest = TestDataGenerator.addGymRequest()
        template.postForEntity("/api", addProductRequest, AddGymResponse::class.java)

        val getSimpleGymResponses =
            template.exchange(
                "/api",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<List<GetSimpleGymResponse>>() {})

        val id = getSimpleGymResponses.body!![0].id

        val request = UpdateGymRequest("name", "address", "description", emptyList())

        val httpEntity = HttpEntity(request)
        template.exchange("/api/$id", HttpMethod.PUT, httpEntity, Any::class.java)
    }

    @Test
    fun `Should return status code 201 and uuid when adding product image`() {
        val imageResource = ClassPathResource("images/pooh.png")
        val file = FileSystemResource(imageResource.uri.toPath())

        val body = LinkedMultiValueMap<String, Any>()
        body.add("image", file)

        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val httpEntity = HttpEntity<MultiValueMap<String, Any>>(body, headers)
        val response = template.postForEntity("/api/image", httpEntity, AddImageResponse::class.java)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)

        val uuid = response.body!!.id
        assertNotNull(uuid)
    }
}

