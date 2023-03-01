package app.gym.domain.gym

import app.gym.domain.franchise.FranchiseRepository
import app.gym.domain.gymTag.GymTag
import app.gym.domain.image.Image
import app.gym.domain.image.ImageRepository
import app.gym.domain.image.ImageStorage
import app.gym.domain.tag.TagRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

class GymNotFoundException : RuntimeException()

class ImageNotFoundException : RuntimeException()

@Service
class GymService(
    private val gymRepository: GymRepository,
    private val franchiseRepository: FranchiseRepository,
    private val imageRepository: ImageRepository,
    private val tagRepository: TagRepository,
    private val imageStorage: ImageStorage,
    private val gymDetailsValidator: GymDetailsValidator,
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
        val gym = Gym()
        // TODO: updateGym과의 중복 코드

        val franchise = when (command.franchiseId) {
            null -> null
            else -> franchiseRepository.findById(command.franchiseId).get()
        }

        val imageIds = command.imageIds
        val images = if (imageIds.isEmpty()) {
            emptyList()
        } else {
            if (imageRepository.countAllByUuidIn(imageIds) != imageIds.size) {
                throw ImageNotFoundException()
            }
            imageRepository.findByUuidIn(imageIds)
        }

        val tags = if (command.tagIds.isEmpty()) {
            emptyList()
        } else {
            tagRepository.findAllById(command.tagIds)
        }
        val gymTags = tags.map { GymTag(it) }

        val details = command.details
        val e = GymDetailsInvalidValueException(details, "gymDetails")
        gymDetailsValidator.validate(details, e)
        if (e.hasErrors()) {
            throw e
        }

        gym.update(
            command.name,
            franchise,
            command.address,
            command.description,
            images,
            command.latitude,
            command.longitude,
            gymTags,
            details
        )
        return gymRepository.save(gym).id!!
    }

    fun deleteGym(id: Long) {
        if (gymRepository.existsById(id))
            gymRepository.deleteById(id)
        else
            throw GymNotFoundException()
    }

    fun addImage(imageFile: MultipartFile): String {
        val id = imageStorage.save(imageFile.inputStream).toString()
        val image = Image.create(id, imageFile.name)
        return imageRepository.save(image).uuid!!
    }

    fun updateGym(command: UpdateGymCommand) {
        val id = command.id
        val gym = if (gymRepository.existsById(id)) {
            gymRepository.findById(id).get()
        } else {
            throw GymNotFoundException()
        }

        val franchise = when (command.franchiseId) {
            null -> null
            else -> franchiseRepository.findById(command.franchiseId).get()
        }

        val imageIds = command.imageIds
        val images = if (imageIds.isEmpty()) {
            emptyList()
        } else {
            if (imageRepository.countAllByUuidIn(imageIds) != imageIds.size) {
                throw ImageNotFoundException()
            }
            imageRepository.findByUuidIn(imageIds)
        }
        val tags = if (command.tagIds.isEmpty()) {
            emptyList()
        } else {
            tagRepository.findAllById(command.tagIds)
        }
        val gymTags: List<GymTag> = tags.map { GymTag(it) }

        val details = command.details
        val e = GymDetailsInvalidValueException(details, "gymDetails")
        gymDetailsValidator.validate(details, e)
        if (e.hasErrors()) {
            throw e
        }

        gym.update(
            command.name,
            franchise,
            command.address,
            command.description,
            images,
            command.latitude,
            command.longitude,
            gymTags,
            details
        )

        gymRepository.save(gym)
    }
}

