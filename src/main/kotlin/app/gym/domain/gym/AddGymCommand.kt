package app.gym.domain.gym

import java.util.*

data class AddGymCommand(
    val title: String,
    val price: Int,
    val description: String,
    val imageIds: List<UUID>
)