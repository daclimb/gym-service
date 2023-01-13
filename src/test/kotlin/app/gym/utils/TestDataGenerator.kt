package app.gym.utils

import app.gym.api.request.AddGymRequest
import app.gym.domain.gym.Gym
import app.gym.domain.image.Image
import java.util.*

class TestDataGenerator {
    companion object {
        fun gym(
            id: Long? = null,
            name: String = "name",
            address: String = "address",
            description: String = "description",
            images: MutableList<Image> = mutableListOf(Image.create(UUID.randomUUID(), "image")),
            latitude: Double = 0.0,
            longitude: Double = 0.0
        ): Gym {
            val gym = Gym(id)
            gym.update(name, address, description, images, latitude, longitude)

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
            address: String = "address",
            description: String = "description",
            imageIds: List<UUID> = emptyList()
        ): AddGymRequest {
            return AddGymRequest(name, address, description, imageIds, 0.0, 0.0)
        }
    }
}