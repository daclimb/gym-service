package app.gym.utils

import app.gym.api.request.AddFranchiseRequest
import app.gym.api.request.AddGymRequest
import app.gym.domain.franchise.Franchise
import app.gym.domain.gym.Gym
import app.gym.domain.image.Image
import java.util.*

class TestDataGenerator {
    companion object {
        fun gym(
            id: Long? = null,
            name: String = "name",
            franchise: Franchise? = null,
            address: String = "address",
            description: String = "description",
            images: MutableList<Image> = mutableListOf(Image.create(UUID.randomUUID(), "image")),
            latitude: Double = 0.0,
            longitude: Double = 0.0
        ): Gym {
            val gym = Gym(id)
            gym.update(name, null, address, description, images, latitude, longitude)

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
            imageIds: List<UUID> = emptyList()
        ): AddGymRequest {
            return AddGymRequest(name, franchiseId, address, description, imageIds, 0.0, 0.0)
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