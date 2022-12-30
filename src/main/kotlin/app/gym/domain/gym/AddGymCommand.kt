package app.gym.domain.gym

import java.util.*

data class AddGymCommand(
    val name: String,
    val franchise: String,
    val address: String,
    val description: String,
    val imageIds: List<UUID>,
    val latitude: Double,
    val longitude: Double
)