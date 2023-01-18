package app.gym.api.request

import app.gym.domain.gym.UpdateGymTagsCommand
import app.gym.domain.gymTag.GymTag

data class UpdateGymTagsRequest(
    val gymTags: List<GymTag>
) {
    fun toCommand(gymId: Long): UpdateGymTagsCommand {
        return UpdateGymTagsCommand(gymId, gymTags)
    }
}