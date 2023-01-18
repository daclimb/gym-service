package app.gym.api.request

import app.gym.domain.gym.AddGymCommand
import java.util.*

data class AddGymRequest(
    val name: String,
    val franchiseId: Long?,
    val address: String,
    val description: String,
    val imageIds: List<UUID>,
    val latitude: Double,
    val longitude: Double,
    val gymTagIds: List<Long>
) {
    fun toCommand(): AddGymCommand {
        return AddGymCommand(name, franchiseId, address, description, imageIds, latitude, longitude, gymTagIds)
    }
}
