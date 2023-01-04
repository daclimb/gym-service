package app.gym.domain.gym

import java.util.*

class UpdateGymCommand(
    val id: Long,
    val name: String,
    val franchiseId: Long?,
    val address: String,
    val description: String,
    val imageIds: List<UUID>,
    val longitude: Double,
    val latitude: Double)
