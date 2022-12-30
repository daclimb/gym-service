package app.gym.domain.gym

import java.util.*

class UpdateGymCommand(
    val id: Long,
    val franchise: String,
    val name: String,
    val address: String,
    val description: String,
    val imageIds: List<UUID>,
    val longitude: Double,
    val latitude: Double)
