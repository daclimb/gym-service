package app.gym.api.request

import app.gym.domain.tag.AddTagCommand

data class AddTagRequest(
    val tag:String
) {
    fun toCommand(): AddTagCommand {
        return AddTagCommand(tag)
    }
}
