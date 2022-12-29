package app.gym.api.response

import app.gym.domain.gym.Gym
import java.util.*

data class GetGymResponse(
    val id: Long,
    val name: String,
    val address: String,
    val description: String?,
    val imageIds: List<UUID>
) {
    companion object {
        fun from(gym: Gym): GetGymResponse {
            return GetGymResponse(
                gym.id!!,
                gym.name,
                gym.address,
                gym.description,
                gym.images.map {
                    it.id!!
                }
            )
        }
    }
}