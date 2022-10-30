package app.gym.domain.gym

import app.gym.config.JPAConfig
import app.gym.domain.image.Image
import app.gym.domain.image.ImageRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@Import(JPAConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GymRepositoryTest {

    @Autowired
    lateinit var gymRepository: GymRepository
    @Autowired
    lateinit var imageRepository: ImageRepository

    @BeforeAll
    fun beforeAll() {
        gymRepository.deleteAll()
        imageRepository.deleteAll()
    }

    @Test
    fun `Product should have createdAt with not null when getting product`() {
        val id = 1L
        val gym = Gym(id)
        gym.title = "title"
        gym.price = 10000
        gym.description = "description"

        gymRepository.saveAndFlush(gym)

        val getProduct = gymRepository.findById(id).get()

        assertNotEquals(LocalDateTime.MIN, getProduct.createdAt)
    }

    @Test
    fun `Image should have createdAt with not null when getting image`() {

        val uuid = UUID.randomUUID()
        val image = Image.create(uuid, "")

        imageRepository.saveAndFlush(image)

        val getProduct = imageRepository.findById(uuid).get()

        assertNotEquals(LocalDateTime.MIN, getProduct.createdAt)
    }
}