package app.gym.api.response

import app.gym.domain.gym.Gym

data class GetGymResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val description: String?
) {
    companion object {
        fun from(gym: Gym): GetGymResponse {
            return GetGymResponse(
                gym.id!!,
                gym.title,
                gym.price,
                gym.description
            )
        }
    }
}