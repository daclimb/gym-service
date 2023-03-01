package app.gym.domain.gym

import app.gym.domain.franchise.FranchiseRepository
import app.gym.domain.image.ImageRepository
import app.gym.domain.image.ImageStorage
import app.gym.domain.tag.TagRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GymServiceTest{

    @MockK
    lateinit var gymRepository: GymRepository
    @MockK
    lateinit var franchiseRepository: FranchiseRepository
    @MockK
    lateinit var imageRepository: ImageRepository
    @MockK
    lateinit var tagRepository: TagRepository
    @MockK
    lateinit var imageStorage: ImageStorage
    @MockK
    lateinit var gymDetailsValidator: GymDetailsValidator

    @InjectMockKs
    lateinit var gymService: GymService


    @Test
    fun `Should throw GymNotFoundException when get gym with id of not existing gym`() {
        every { gymRepository.existsById(any()) } returns false

        assertThrows<GymNotFoundException> { gymService.getGym(0L) }
    }

    @Test
    fun `Should throw GymNotFoundException when update gym with id of not existing gym`() {
        every { gymRepository.existsById(any()) } returns false
        val command = UpdateGymCommand(0L, "name", null, "address", "description", emptyList(), 0.0, 0.0, emptyList(), GymDetails())

        assertThrows<GymNotFoundException> { gymService.updateGym(command) }
    }

    @Test
    fun `Should throw GymNotFoundException when delete gym with id of not existing gym`() {
        every { gymRepository.existsById(any()) } returns false

        assertThrows<GymNotFoundException> { gymService.deleteGym(0L) }
    }
}