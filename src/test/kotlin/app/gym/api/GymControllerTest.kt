package app.gym.api

import app.gym.api.controller.GymController
import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.config.SecurityConfig
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.GymService
import app.gym.domain.member.UserRole
import app.gym.domain.member.WithCustomMockUser
import app.gym.security.JwtAuthenticationFilter
import app.gym.util.JsonUtils
import app.gym.utils.TestDataGenerator
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import restdocs.RestdocsType
import restdocs.andDocument
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

    @MockkBean
    private lateinit var gymService: GymService

    @Test
    fun `Should return status code 200 and gym details when get gym`() {
        val gym = TestDataGenerator.gym(1L)
        every { gymService.getGym(any()) } returns gym

        val result = mvc.perform(get("/api/gym/{gymId}", 1))

        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(gym.id))
            .andExpect(jsonPath("$.name").value(gym.name))
            .andExpect(jsonPath("$.franchiseName").value(gym.franchise!!.name))
            .andExpect(jsonPath("$.address").value(gym.address))
            .andExpect(jsonPath("$.description").value(gym.description))
            .andExpect(jsonPath("$.imageIds").value(gym.images.map { it.id!!.toString() }))
            .andExpect(jsonPath("$.latitude").value(gym.latitude))
            .andExpect(jsonPath("$.longitude").value(gym.longitude))

        // document
        result.andDocument("GetGym") {
            tags = setOf("Gym")

            request {
                pathParam("gymId") {
                    type = RestdocsType.NUMBER
                    description = "id of the gym"
                }
            }
            response("GetGymResponse") {
                field("id") {
                    type = RestdocsType.NUMBER
                    description = "id of the gym"
                }
                field("name") {
                    type = RestdocsType.STRING
                    description = "name of the gym"
                }
                field("franchiseName") {
                    type = RestdocsType.STRING
                    description = "franchise name of the gym"
                }
                field("address") {
                    type = RestdocsType.STRING
                    description = "address of the gym"
                }
                field("description") {
                    type = RestdocsType.STRING
                    description = "description of the gym"
                }
                field("imageIds") {
                    type = RestdocsType.STRING_ARRAY
                    description = "image ids of the gym"
                }
                field("latitude") {
                    type = RestdocsType.NUMBER
                    description = "latitude of the gym"
                }
                field("longitude") {
                    type = RestdocsType.NUMBER
                    description = "longitude of the gym"
                }
            }
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
                tags = setOf("Gym")

                response("GetGymListResponse") {
                    array("gyms") {
                        field("id") {
                            type = RestdocsType.NUMBER
                            description = "id of the gym"
                        }
                        field("name") {
                            type = RestdocsType.STRING
                            description = "name of the gym"
                        }
                        field("address") {
                            type = RestdocsType.STRING
                            description = "address of the gym"
                        }
                        field("thumbnail") {
                            type = RestdocsType.STRING
                            description = "thumbnail uuid of the gym"
                        }
                    }
                }
            }
        }
    }

    @Test
    @WithCustomMockUser(userRole = UserRole.Admin)
    fun `Should return status code 201 when add gym`() {
        val request = AddGymRequest("name", 1L, "address", "description", emptyList(), 0.0, 0.0, emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.addGym(any()) } returns 1L

        val result = mvc.perform(
            post("/api/gym")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isCreated)

        result.andDocument("AddGym") {
            tags = setOf("Gym")

            request("AddGymRequest") {
                field("name") {
                    type = RestdocsType.STRING
                    description = "name of the gym"
                }
                field("franchiseId") {
                    type = RestdocsType.NUMBER
                    description = "franchise id of the gym"
                }
                field("address") {
                    type = RestdocsType.STRING
                    description = "address of the gym"
                }
                field("description") {
                    type = RestdocsType.STRING
                    description = "description of the gym"
                }
                field("imageIds") {
                    type = RestdocsType.STRING_ARRAY
                    description = "image ids of the gym"
                }
                field("latitude") {
                    type = RestdocsType.NUMBER
                    description = "latitude of the gym"
                }
                field("longitude") {
                    type = RestdocsType.NUMBER
                    description = "longitude of the gym"
                }
                field("tagIds") {
                    type = RestdocsType.NUMBER_ARRAY
                    description = "tag ids of the gym"
                }
            }
            response("AddGymResponse") {
                field("gymId") {
                    type = RestdocsType.NUMBER
                    description = "id of the created gym"
                }
            }
        }
    }

    @Test
    fun `Should return status code 400 when delete gym with id of not existing gym`() {
        every { gymService.deleteGym(any()) } throws GymNotFoundException()

        val result = mvc.perform(delete("/api/gym/{gymId}", 1))

        result.andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return status code 200 when update gym`() {
        val request = UpdateGymRequest("name", 1L, "address", "description", emptyList(), 0.0, 0.0, emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.updateGym(any()) } returns Unit

        val result = mvc.perform(
            put("/api/gym/{gymId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isOk)

        result.andDocument("UpdateGym") {
            tags = setOf("Gym")

            request("UpdateGymRequest") {
                pathParam("gymId") {
                    type = RestdocsType.NUMBER
                    description = "id of the gym"
                }
                field("name") {
                    type = RestdocsType.STRING
                    description = "name of the gym"
                }
                field("franchiseId") {
                    type = RestdocsType.NUMBER
                    description = "franchise id of the gym"
                }
                field("address") {
                    type = RestdocsType.STRING
                    description = "address of the gym"
                }
                field("description") {
                    type = RestdocsType.STRING
                    description = "description of the gym"
                }
                field("imageIds") {
                    type = RestdocsType.STRING_ARRAY
                    description = "image ids of the gym"
                }
                field("latitude") {
                    type = RestdocsType.NUMBER
                    description = "latitude of the gym"
                }
                field("longitude") {
                    type = RestdocsType.NUMBER
                    description = "longitude of the gym"
                }
                field("tagIds") {
                    type = RestdocsType.NUMBER_ARRAY
                    description = "tag ids of the gym"
                }
            }
        }
    }

    @Test
    fun `Should return status code 400 when update gym with id of not existing gym`() {
        val request = UpdateGymRequest("name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.updateGym(any()) } throws GymNotFoundException()

        val result = mvc.perform(
            put("/api/gym/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return status code 201 when add image`() {
        val imageResource = ClassPathResource("images/pooh.png")
        val file = Files.readAllBytes(imageResource.uri.toPath())

        val uuid = UUID.randomUUID().toString()

        every { gymService.addImage(any()) } returns uuid

        val result = mvc.perform(
            multipart("/api/gym/image")
                .file("image", file)
        )

        result.andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(uuid.toString()))

        result.andDocument("AddGymImage") {
            tags = setOf("Gym")
            request {
                requestParts(
                    partWithName("image").description("gym image")
                )
            }
            response("AddImageResponse") {
                field("id") {
                    type = RestdocsType.STRING
                    description = "uuid of the image"
                }
            }
        }
    }
}