package app.gym.api.response

import app.gym.domain.gym.Gym
import app.gym.domain.gym.GymDetails

data class GetGymResponse(
    val id: Long,
    val name: String,
    val franchiseName: String?,
    val address: String,
    val description: String?,
    val imageIds: List<String>,
    val latitude: Double,
    val longitude: Double,
    val details: GymDetails
) {
    companion object {
        fun from(gym: Gym): GetGymResponse {
            return GetGymResponse(
                gym.id!!,
                gym.name,
                gym.franchise?.name,
                gym.address,
                gym.description,
                gym.images.map { it.id!! },
                gym.latitude,
                gym.longitude,
                gym.details
            )
        }
    }
}