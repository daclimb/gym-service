package app.gym.api.request

import app.gym.domain.gym.UpdateGymCommand

data class UpdateGymRequest(
    val name: String,
    val franchiseId: Long?,
    val address: String,
    val description: String,
    val imageIds: List<String>,
    val latitude: Double,
    val longitude: Double,
    val tagIds: List<Long>,
) {
    fun toCommand(id: Long): UpdateGymCommand {
        return UpdateGymCommand(id, name, franchiseId, address, description, imageIds, latitude, longitude, tagIds)
    }

}
