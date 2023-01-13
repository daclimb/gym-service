package app.gym.domain.franchise

data class UpdateFranchiseCommand(
    val id: Long,
    val name: String,
    val description: String
)
