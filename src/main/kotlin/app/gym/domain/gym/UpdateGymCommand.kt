package app.gym.domain.gym

data class UpdateGymCommand(
    val id: Long,
    val name: String,
    val franchiseId: Long?,
    val address: String,
    val description: String,
    val imageIds: List<String>,
    val latitude: Double,
    val longitude: Double,
    val tagIds: List<Long>,
    val details: GymDetails
) {
}
