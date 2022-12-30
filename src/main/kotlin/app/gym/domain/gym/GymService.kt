package app.gym.domain.gym

import app.gym.domain.image.Image
import app.gym.domain.image.ImageRepository
import app.gym.domain.image.ImageStorage
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

class GymNotFoundException : Exception()
class ImageNotFoundException : Exception()
class NullIdException : Exception()

@Service
class GymService(
    private val gymRepository: GymRepository,
    private val imageRepository: ImageRepository,
    private val imageStorage: ImageStorage
) {
    fun getGym(id: Long): Gym {
        return if (gymRepository.existsById(id)) {
            gymRepository.findById(id).get()
        } else {
            throw GymNotFoundException()
        }
    }

    fun getGyms(): List<Gym> {
        return gymRepository.findAll()
    }

    fun addGym(command: AddGymCommand): Long {
        val imageIds = command.imageIds
        val images = if (imageIds.isEmpty()) {
            emptyList()
        } else {
            imageRepository.findByUuidIn(imageIds)
        }

        val gym = Gym()
        gym.update(command.name, command.franchise, command.address, command.description, images, command.latitude, command.longitude)
        return gymRepository.save(gym).id!!
    }

    fun deleteGym(id: Long) {
        if (gymRepository.existsById(id))
            gymRepository.deleteById(id)
        else
            throw GymNotFoundException()
    }

    fun addImage(imageFile: MultipartFile): UUID {
        val id = imageStorage.save(imageFile.inputStream)
        val image = Image.create(id, imageFile.name)
        return imageRepository.save(image).uuid!!
    }

    fun updateGym(command: UpdateGymCommand) {
        val id = command.id
        val gym = if(gymRepository.existsById(id)) {
            gymRepository.findById(id).get()
        } else {
            throw GymNotFoundException()
        }

        val imageIds = command.imageIds
        val images = if (imageIds.isEmpty()) {
            emptyList()
        } else {
            imageRepository.findByUuidIn(imageIds)
        }

        gym.update(command.name, command.franchise, command.address, command.description, images, command.latitude, command.longitude)

        gymRepository.save(gym)
    }
}













