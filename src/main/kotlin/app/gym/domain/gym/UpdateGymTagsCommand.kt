package app.gym.domain.gym

import app.gym.domain.gymTag.GymTag

data class UpdateGymTagsCommand(
    val gymId: Long,
    val gymTags: List<GymTag>,
)