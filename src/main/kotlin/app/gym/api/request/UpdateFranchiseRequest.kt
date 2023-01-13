package app.gym.api.request

import app.gym.domain.franchise.UpdateFranchiseCommand

data class UpdateFranchiseRequest(
    val name: String,
    val description: String
) {
    fun toCommand(id: Long): UpdateFranchiseCommand {
        return UpdateFranchiseCommand(id, name, description)
    }
}
