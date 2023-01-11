package app.gym.api.response

import app.gym.domain.gym.Gym
import java.util.*

data class GetGymListResponse(
    val gyms: List<SimpleGym>,
) : Response {

    data class SimpleGym(
        val id: Long,
        val name: String,
        val address: String,
        val thumbnail: UUID?,
    )

    companion object {
        fun from(gyms: List<Gym>): GetGymListResponse {
            return GetGymListResponse(gyms.map {
                val thumbnail: UUID? =
                    if (it.images.size == 0) {
                        null
                    } else {
                        it.images[0].uuid
                    }

                SimpleGym(
                    it.id!!,
                    it.name,
                    it.address,
                    thumbnail
                )
            })
        }
    }
}