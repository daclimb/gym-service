package app.gym.api.response

import app.gym.domain.gym.Gym
import java.util.*

data class GetSimpleGymResponse(
    val id: Long,
    val name: String,
    val address: String,
    val thumbnail: UUID?
) {
    companion object {
        fun from(gym: Gym): GetSimpleGymResponse {

            val  thumbnail: UUID? = if (gym.images.size == 0) {
                null
            } else {
                gym.images[0].uuid
            }

            return GetSimpleGymResponse(
                gym.id!!,
                gym.name,
                gym.address,
                thumbnail
            )
        }

    }
}