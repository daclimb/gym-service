package app.gym.api.response

import app.gym.domain.gym.Gym

data class GetGymListResponse(
    val gyms: List<SimpleGym>,
) {

    data class SimpleGym(
        val id: Long,
        val name: String,
        val address: String,
        val thumbnail: String?,
        val tagIds: List<Long>
    )

    companion object {
        fun from(gyms: List<Gym>): GetGymListResponse {
            return GetGymListResponse(gyms.map { gym ->
                val thumbnail: String? =
                    if (gym.images.size == 0) {
                        null
                    } else {
                        gym.images[0].uuid
                    }

                SimpleGym(
                    gym.id!!,
                    gym.name,
                    gym.address,
                    thumbnail,
                    gym.gymTags.map { it.tag.id!! }
                )
            })
        }
    }
}