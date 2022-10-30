package app.gym.api.response

import app.gym.domain.gym.Gym

class GetSimpleGymResponse(
    val id: Long,
    val title: String,
    val price: Int
) {
    companion object {
        fun from(gym: Gym): GetSimpleGymResponse {
            return GetSimpleGymResponse(
                gym.id!!,
                gym.title,
                gym.price
            )
        }

    }
}