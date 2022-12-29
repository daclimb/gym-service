package app.gym.api.request

import app.gym.domain.gym.UpdateGymCommand
import java.util.*

class UpdateGymRequest(
    val name: String,
    val address: String,
    val description: String,
    val imageIds: List<UUID>
) {
    fun toCommand(id: Long): UpdateGymCommand {
        return UpdateGymCommand(id, name, address, description, imageIds)

    }

}
