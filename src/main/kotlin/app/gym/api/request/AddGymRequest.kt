package app.gym.api.request

import app.gym.domain.gym.AddGymCommand
import java.util.*

data class AddGymRequest(
    val title: String,
    val price: Int,
    val description: String,
    val imageIds: List<UUID>
) {
    fun toCommand(): AddGymCommand {
        return AddGymCommand(title, price, description, imageIds)
    }
}
