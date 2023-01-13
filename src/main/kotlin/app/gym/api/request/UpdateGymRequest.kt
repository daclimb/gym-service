package app.gym.api.request

import app.gym.domain.gym.UpdateGymCommand
import java.util.*

data class UpdateGymRequest(
    val name: String,
    val franchiseId: Long?,
    val address: String,
    val description: String,
    val imageIds: List<UUID>,
    val latitude: Double,
    val longitude: Double
) {
    fun toCommand(id: Long): UpdateGymCommand {
        return UpdateGymCommand(id, name, franchiseId, address, description, imageIds, latitude, longitude)
    }

}
