package app.gym.api.request

import app.gym.domain.gym.AddGymCommand
import app.gym.domain.gym.GymDetails

data class AddGymRequest(
    val name: String,
    val franchiseId: Long?,
    val address: String,
    val description: String,
    val imageIds: List<String>,
    val latitude: Double,
    val longitude: Double,
    val tagIds: List<Long>,
    val details: GymDetails
) {
    fun toCommand(): AddGymCommand {
        return AddGymCommand(name, franchiseId, address, description, imageIds, latitude, longitude, tagIds, details)
    }
}
