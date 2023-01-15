package app.gym.api

import app.gym.api.controller.GymController
import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.config.SecurityConfig
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.GymService
import app.gym.domain.member.MemberRole
import app.gym.domain.member.WithMockMember
import app.gym.jwt.JwtAuthenticationFilter
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
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
class GymControllerTest {

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

        val result = mvc.perform(get("/api/gym/{gymId}", 1))

        result.andExpect{
            status().isOk
            jsonPath("$.id").value(gym.id)
            jsonPath("$.name").value(gym.name)
            jsonPath("$.address").value(gym.address)
            jsonPath("$.description").value(gym.description)
            jsonPath("$.imageIds").value(gym.images.map { it.id!!.toString() })
            jsonPath("$.latitude").value(gym.latitude)
            jsonPath("$.longitude").value(gym.longitude)
        }

        // document
        result.andDocument("GetGym") {
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
                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("latitude of the gym"),
                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("longitude of the gym")
            )
        }
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, 3L])
    fun `Should return status code 200 when get all simple gyms`(length: Long) {
        val gyms = TestDataGenerator.gyms(length)

        every { gymService.getGyms() } returns gyms

        val result = mvc.perform(get("/api/gym"))

        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.gyms.length()").value(length))

        if (length == 3L) {
            result.andDocument("GetGymList") {
                responseSchema(Schema("GetGymListResponse"))
                responseFields(
                    fieldWithPath("gyms[].id").type(JsonFieldType.NUMBER).description("id of the gym"),
                    fieldWithPath("gyms[].name").type(JsonFieldType.STRING).description("name of the gym"),
                    fieldWithPath("gyms[].address").type(JsonFieldType.STRING).description("address of the gym"),
                    fieldWithPath("gyms[].thumbnail").type(JsonFieldType.STRING)
                        .description("thumbnail uuid of the gym"),
                )
            }
        }
    }

    @Test
    @WithMockMember(memberRole = MemberRole.Admin)
    fun `Should return status code 201 when add gym`() {

        val request = AddGymRequest("name", 1L, "address", "description", emptyList(), 0.0, 0.0)
        val content = JsonUtils.toJson(request)
        every { gymService.addGym(any()) } returns 1L

        val result = mvc.perform(
            post("/api/gym")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect {
            status().isCreated
        }

        result.andDocument("AddGym") {
            requestSchema(Schema("AddGymRequest"))
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the gym"),
                fieldWithPath("franchiseId").type(JsonFieldType.NUMBER).description("franchise id of the gym"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("address of the gym"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description of the gym"),
                fieldWithPath("imageIds").type(JsonFieldType.ARRAY).description("image ids of the gym"),
                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("latitude of the gym"),
                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("longitude of the gym")
            )
            responseSchema(Schema("AddGymResponse"))
            responseFields(
                fieldWithPath("gymId").type(JsonFieldType.NUMBER).description("id of the created gym")
            )
        }
    }

    @Test
    fun `Should return status code 400 when delete gym with id of not existing gym`() {
        every { gymService.deleteGym(any()) } throws GymNotFoundException()
        mvc.perform(delete("/api/gym/1"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return status code 200 when update gym`() {
        val request = UpdateGymRequest("name", 1L, "address", "description", emptyList(), 0.0, 0.0)
        val content = JsonUtils.toJson(request)
        every { gymService.updateGym(any()) } returns Unit

        val result = mvc.perform(
            put("/api/gym/{gymId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isOk)

        result.andDocument("UpdateGym") {
            pathParameters(
                parameterWithName("gymId").type(SimpleType.INTEGER).description("id of the gym")
            )
            requestSchema(Schema("UpdateGymRequest"))
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the gym"),
                fieldWithPath("franchiseId").type(JsonFieldType.NUMBER).description("franchise id of the gym"),
                fieldWithPath("address").type(JsonFieldType.STRING).description("address of the gym"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description of the gym"),
                fieldWithPath("imageIds").type(JsonFieldType.ARRAY).description("image ids of the gym"),
                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("latitude of the gym"),
                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("longitude of the gym")
            )
        }
    }

    @Test
    fun `Should return status code 400 when update gym with id of not existing gym`() {
        val request = UpdateGymRequest("name", null, "address", "description", emptyList(), 0.0, 0.0)
        val content = JsonUtils.toJson(request)
        every { gymService.updateGym(any()) } throws GymNotFoundException()

        mvc.perform(
            put("/api/gym/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return status code 201 when add image`() {
        val imageResource = ClassPathResource("images/pooh.png")
        val file = Files.readAllBytes(imageResource.uri.toPath())

        val uuid = UUID.randomUUID()

        every { gymService.addImage(any()) } returns uuid

        val result = mvc.perform(
            multipart("/api/gym/image")
                .file("image", file)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(uuid.toString()))

        result.andDocument("AddImageImage") {
            requestParts(
                partWithName("image").description("gym image")
            )
            responseSchema(Schema("AddImageResponse"))
            responseFields(
                fieldWithPath("id").type(JsonFieldType.STRING).description("uuid of the image")
            )
        }
    }
}