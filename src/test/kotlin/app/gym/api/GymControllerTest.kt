package app.gym.api

import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.GetSimpleGymResponse
import app.gym.config.JwtAuthenticationFilter
import app.gym.config.SecurityConfig
import app.gym.domain.gym.Gym
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.GymService
import app.gym.domain.member.WithMockUser
import app.gym.util.JsonUtils
import app.gym.utils.TestDataGenerator
import app.gym.utils.andDocument
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.file.Files
import java.util.*
import kotlin.io.path.toPath

@WebMvcTest(
    controllers = [GymController::class],
    excludeFilters = [
        ComponentScan.Filter(
            classes = [JwtAuthenticationFilter::class],
            type = FilterType.ASSIGNABLE_TYPE
        )]
)
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
internal class GymControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @TestConfiguration
    class Config {
        @Bean
        fun gymService() = mockk<GymService>()
    }

    @Autowired
    private lateinit var gymService: GymService

    @Test
    fun `Should return status code 200 and gym details when get gym`() {
        val gym = TestDataGenerator.gym(1L)
        every { gymService.getGym(any()) } returns gym

        val result = mvc.perform(get("/api/{gymId}", 1))

        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(gym.id))
            .andExpect(jsonPath("$.name").value(gym.name))
            .andExpect(jsonPath("$.address").value(gym.address))
            .andExpect(jsonPath("$.description").value(gym.description))

        // document
        result.andDocument("getGym") {
            pathParameters(
                parameterWithName("gymId").type(SimpleType.INTEGER).description("Id of the gym")
            )
            responseSchema(Schema("GetGymResponse"))
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id of the gym"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the gym"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("address of the gym"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description of the gym"),
                fieldWithPath("imageIds").type(JsonFieldType.ARRAY).description("image ids of the gym"),
                fieldWithPath("imageIds[].*").type(JsonFieldType.STRING).description("uuid of the gym image")
            )
        }
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, 3L])
    fun `Should return status code 200 when get all simple products`(length: Long) {
        val products = TestDataGenerator.gyms(length)

        every { gymService.getGyms() } returns products

        mvc.get("/api")
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(length) }
            }
    }

    @Test
    @WithMockUser(userId = 1L)
    fun `Should return status code 201 when add product`() {
        val request = AddGymRequest("name", "address", "description", emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.addGym(any()) } returns Unit

        mvc.post("/api/gym") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = content
        }.andExpect {
            status { isCreated() }
        }.andDo { print() }
    }

    @Test
    fun `Should return status code 400 when delete product with id of not existing product`() {

        every { gymService.deleteGym(any()) } throws GymNotFoundException()

        mvc.delete("/api/1")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `Should return status code 200 when update product`() {
        val request = UpdateGymRequest("name", "address", "description", emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.updateGym(any()) } returns Unit

        mvc.put("/api/1") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = content
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `Should return status code 400 when update product with id of not existing product`() {
        val request = UpdateGymRequest("name", "address", "description", emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.updateGym(any()) } throws GymNotFoundException()

        mvc.put("/api/1") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = content
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun addImage() {
        val imageResource = ClassPathResource("images/pooh.png")
        val file = Files.readAllBytes(imageResource.uri.toPath())

        val uuid = UUID.randomUUID()

        every { gymService.addImage(any()) } returns uuid

        mvc.multipart("/api/image") {
            this.file("image", file)
        }.andExpect {
            status { isCreated() }
            content { jsonPath("$.id") { this.value(uuid.toString()) } }
        }
    }

    @Test
    fun mapTest() {
        val gyms = emptyList<Gym>()
        val gymResponseList = gyms.map {
            GetSimpleGymResponse.from(it)
        }
    }
}