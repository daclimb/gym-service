package app.gym.api.request

import app.gym.domain.gym.UpdateGymCommand
import java.util.*

class UpdateGymRequest(
    val title: String,
    val price: Int,
    val description: String,
    val imageIds: List<UUID>
) {
    fun toCommand(id: Long): UpdateGymCommand {
        return UpdateGymCommand(id, title, price, description, imageIds)

    }

}
