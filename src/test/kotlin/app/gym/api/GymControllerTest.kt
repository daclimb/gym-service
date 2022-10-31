package app.gym.api

import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.GetSimpleGymResponse
import app.gym.domain.gym.Gym
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.GymService
import app.gym.utils.JsonUtils
import app.gym.utils.TestDataGenerator
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import java.nio.file.Files
import java.util.*
import kotlin.io.path.toPath

@WebMvcTest
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
    fun `Should return status code 200 and product details when get product`() {
        val gym = TestDataGenerator.gym(1L)
        every { gymService.getGym(any()) } returns gym

        mvc.get("/api/1")
            .andExpect {
                status { isOk() }
                this.jsonPath("$.id") { value(gym.id) }
                this.jsonPath("$.title") { value(gym.name) }
                this.jsonPath("$.price") { value(gym.address) }
                this.jsonPath("$.description") { value(gym.description) }
                this.jsonPath("$.images") {
                    if (gym.images.size > 0) {
                        value(gym.images)
                    } else {
                        doesNotExist()
                    }
                }
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
    fun `Should return status code 201 when add product`() {
        val request = AddGymRequest("title", 10000, "description", emptyList())
        val content = JsonUtils.toJson(request)
        every { gymService.addGym(any()) } returns Unit

        mvc.post("/api") {
            this.contentType = MediaType.APPLICATION_JSON
            this.content = content
        }.andExpect {
            status { isCreated() }
        }
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
        val request = UpdateGymRequest("title", 10000, "description", emptyList())
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
        val request = UpdateGymRequest("title", 10000, "description", emptyList())
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