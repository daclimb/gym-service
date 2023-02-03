package app.gym.util

import app.gym.api.request.AddFranchiseRequest
import app.gym.api.request.AddGymRequest
import app.gym.domain.franchise.Franchise
import app.gym.domain.gym.Gym
import app.gym.domain.gymTag.GymTag
import app.gym.domain.image.Image
import app.gym.domain.tag.Tag
import java.util.*

class TestDataGenerator {
    companion object {
        fun gym(
            id: Long? = null,
            name: String = "name",
            franchise: Franchise? = Franchise(1L).apply { updateDetails("franchise", "description") },
            address: String = "address",
            description: String = "description",
            images: MutableList<Image> = mutableListOf(Image.create(UUID.randomUUID().toString(), "image")),
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            gymTags: List<GymTag> = listOf(GymTag(Tag(null, "tag")))
        ): Gym {
            val gym = Gym(id)
            gym.update(name, franchise, address, description, images, latitude, longitude, gymTags)

            return gym
        }

        fun gyms(
            len: Long = 3L
        ): List<Gym> {
            val gyms = mutableListOf<Gym>()
            for (i: Long in 1L..len)
                gyms.add(gym(i))
            return gyms.toList()
        }

        fun addGymRequest(
            name: String = "name",
            franchiseId: Long? = null,
            address: String = "address",
            description: String = "description",
            imageIds: List<String> = emptyList(),
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            gymTags: List<Long> = emptyList()
        ): AddGymRequest {
            return AddGymRequest(name, franchiseId, address, description, imageIds, latitude, longitude, gymTags)
        }

        fun franchise(
            id: Long? = null,
            name: String = "name",
            description: String = "description",
            gyms: List<Gym> = listOf(Gym(1L))
        ): Franchise {
            val franchise = Franchise(id)
            franchise.updateDetails(name, description)
            franchise.updateGyms(gyms)
            return franchise
        }

        fun franchiseList(
            len: Long = 3L
        ): List<Franchise> {
            val franchiseList = mutableListOf<Franchise>()
            for (i: Long in 1L..len)
            {
                franchiseList.add(franchise(i))
            }
            return franchiseList.toList()
        }

        fun addFranchiseRequest(
            name: String = "name",
            description: String = "description"
        ): AddFranchiseRequest {
            return AddFranchiseRequest(name, description)
        }
    }
}