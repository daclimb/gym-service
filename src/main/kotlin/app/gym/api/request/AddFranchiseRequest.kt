package app.gym.api.request

import app.gym.domain.franchise.AddFranchiseCommand

data class AddFranchiseRequest(
    val name: String,
    val description: String
) {
    fun toCommand(): AddFranchiseCommand {
        return AddFranchiseCommand(name, description)
    }
}
