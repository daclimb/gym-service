package app.gym.domain.gym

import java.util.*

class UpdateGymCommand(
    val id: Long,
    val title: String,
    val price: Int,
    val description: String,
    val imageIds: List<UUID>)
