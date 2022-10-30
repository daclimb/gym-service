package app.gym.domain.gym

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ExtendWith(MockKExtension::class)
class GymServiceTest {

    @MockK
    lateinit var gymRepository: GymRepository

    @Autowired
    lateinit var gymService: GymService

    @Test
    fun `Should throw ProductNotFoundException when get product with id of not existing product`() {
        every { gymRepository.existsById(any()) } returns false

        assertThrows<GymNotFoundException> { gymService.getGym(0L) }
    }

    @Test
    fun `Should throw ProductNotFoundException when update product with id of not existing product`() {
        every { gymRepository.existsById(any()) } returns false
        val command = UpdateGymCommand(0L, "title", 10000, "description", emptyList())

        assertThrows<GymNotFoundException> { gymService.updateGym(command) }
    }

    @Test
    fun `Should throw ProductNotFoundException when delete product with id of not existing product`() {
        every { gymRepository.existsById(any()) } returns false

        assertThrows<GymNotFoundException> { gymService.deleteGym(0) }
    }
}