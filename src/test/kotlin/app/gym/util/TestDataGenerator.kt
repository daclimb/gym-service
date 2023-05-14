package app.gym.util

import app.gym.api.request.AddFranchiseRequest
import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.domain.franchise.Franchise
import app.gym.domain.gym.Gym
import app.gym.domain.gym.GymDetails
import app.gym.domain.gymTag.GymTag
import app.gym.domain.image.Image
import app.gym.domain.tag.Tag
import java.util.*

class TestDataGenerator {
    companion object {
        private val gymDetails: GymDetails = GymDetails(
            "010-0000-0000",
            "qwer1234",
            listOf(GymDetails.Price("일일 이용", 20000), GymDetails.Price("1개월", 120000)),
            listOf("#000000", "#111111", "#222222"),
            listOf("수건", "샤워실", "세족실", "락커", "요가매트"),
            listOf("문보드", "킬터보드", "행보드", "캠퍼싱보드"),
            GymDetails.OpeningHours(
                listOf("17:00", "23:00"),
                listOf("10:00", "23:00"),
                listOf("10:00", "23:00"),
                listOf("10:00", "23:00"),
                listOf("10:00", "23:00"),
                listOf("10:00", "23:00"),
                listOf("10:00", "23:00"),
                listOf("10:00", "23:00")
            ),
            300
        )

        fun gym(
            id: Long? = null,
            name: String = "name",
            franchise: Franchise? = Franchise(1L).apply { updateDetails("franchise", "description") },
            address: String = "address",
            description: String = "description",
            images: MutableList<Image> = mutableListOf(Image.create(UUID.randomUUID().toString(), "image")),
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            gymTags: List<GymTag> = listOf(GymTag(Tag(1L, "tag"))),
            details: GymDetails = gymDetails
        ): Gym {
            val gym = Gym(id)
            gym.update(name, franchise, address, description, images, latitude, longitude, gymTags, details)

            return gym
        }

        fun gyms(
            len: Long = 3L,
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
            gymTags: List<Long> = emptyList(),
            details: GymDetails = gymDetails
        ): AddGymRequest {
            return AddGymRequest(
                name,
                franchiseId,
                address,
                description,
                imageIds,
                latitude,
                longitude,
                gymTags,
                details
            )
        }

        fun updateGymRequest(
            name: String = "name",

            franchiseId: Long? = null,
            address: String = "address",
            description: String = "description",
            imageIds: List<String> = emptyList(),
            latitude: Double = 0.0,
            longitude: Double = 0.0,
            gymTags: List<Long> = emptyList(),
            details: GymDetails = gymDetails
        ): UpdateGymRequest {
            return UpdateGymRequest(
                name,
                franchiseId,
                address,
                description,
                imageIds,
                latitude,
                longitude,
                gymTags,
                details
            )
        }

        fun franchise(
            id: Long? = null,
            name: String = "name",
            description: String = "description",
            gyms: List<Gym> = listOf(Gym(1L)),
        ): Franchise {
            val franchise = Franchise(id)
            franchise.updateDetails(name, description)
            franchise.updateGyms(gyms)
            return franchise
        }

        fun franchiseList(
            len: Long = 3L,
        ): List<Franchise> {
            val franchiseList = mutableListOf<Franchise>()
            for (i: Long in 1L..len) {
                franchiseList.add(franchise(i))
            }
            return franchiseList.toList()
        }

        fun addFranchiseRequest(
            name: String = "name",
            description: String = "description",
        ): AddFranchiseRequest {
            return AddFranchiseRequest(name, description)
        }
    }
}