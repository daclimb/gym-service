package app.gym.domain.gym

import app.gym.domain.franchise.FranchiseRepository
import app.gym.domain.gymTag.GymTag
import app.gym.domain.gymTag.GymTagRepository
import app.gym.domain.image.Image
import app.gym.domain.image.ImageRepository
import app.gym.domain.image.ImageStorage
import app.gym.domain.tag.TagRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

class GymNotFoundException : RuntimeException()

@Service
class GymService(
    private val gymRepository: GymRepository,
    private val franchiseRepository: FranchiseRepository,
    private val imageRepository: ImageRepository,
    private val gymTagRepository: GymTagRepository,
    private val tagRepository: TagRepository,
    private val imageStorage: ImageStorage,
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

        val franchise = if (command.franchiseId != null) {
            franchiseRepository.findById(command.franchiseId).get()
        } else {
            null
        }

        val tags = tagRepository.findAllById(command.gymTags)
        val gymTags = tags.map { GymTag(it) }

        gym.update(
            command.name,
            franchise,
            command.address,
            command.description,
            images,
            command.latitude,
            command.longitude,
            gymTags
        )
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
            imageRepository.findByUuidIn(imageIds)
        }

        val tags = if (command.tagIds.isEmpty()) {
            emptyList()
        } else {
            tagRepository.findAllById(command.tagIds)
        }
        val gymTags: List<GymTag> = tags.map { GymTag(it) }

        gym.update(
            command.name,
            franchise,
            command.address,
            command.description,
            images,
            command.latitude,
            command.longitude,
            gymTags
        )

        gymRepository.save(gym)
    }

//    fun getGymsWithTags(tags: List<Long>): List<Gym> {
//        return gymRepository.findGymsByGymTagsTagId()
//    } // TODO
}