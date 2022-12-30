package app.gym.api.request

import app.gym.domain.gym.AddGymCommand
import java.util.*

data class AddGymRequest(
    val name: String,
    val franchise: String,
    val address: String,
    val description: String,
    val imageIds: List<UUID>,
    val latitude: Double,
    val longitude: Double
) {
    fun toCommand(): AddGymCommand {
        return AddGymCommand(name, franchise, address, description, imageIds, latitude, longitude)
    }
}
