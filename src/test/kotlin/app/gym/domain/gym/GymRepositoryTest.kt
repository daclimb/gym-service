package app.gym.domain.gym

import app.gym.config.JPATestConfig
import app.gym.config.TestContainersConfig
import app.gym.domain.image.Image
import app.gym.domain.image.ImageRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.testcontainers.containers.DockerComposeContainer
import java.time.LocalDateTime
import java.util.*
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
@Import(JPATestConfig::class, TestContainersConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GymRepositoryTest {

    @Autowired
    lateinit var gymRepository: GymRepository
    @Autowired
    lateinit var imageRepository: ImageRepository
    companion object {
        @Autowired
        @JvmStatic
        private lateinit var container: DockerComposeContainer<*>
    }

    @BeforeAll
    fun beforeAll() {
        gymRepository.deleteAll()
        imageRepository.deleteAll()
    }

    @Test
    fun `Gym should have createdAt with not null when getting gym`() {
        val id = 1L
        val gym = Gym(id)
        gym.update("name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList())

        gymRepository.saveAndFlush(gym)

        val getGym = gymRepository.findById(id).get()

        assertNotEquals(LocalDateTime.MIN, getGym.createdAt)
    }

    @Test
    fun `Image should have createdAt with not null when getting image`() {

        val uuid = UUID.randomUUID().toString()
        val image = Image.create(uuid, "")

        imageRepository.saveAndFlush(image)

        val getGym = imageRepository.findById(uuid).get()

        assertNotEquals(LocalDateTime.MIN, getGym.createdAt)
    }
}