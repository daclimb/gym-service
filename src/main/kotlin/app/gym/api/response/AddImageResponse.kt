package app.gym.api.response

import java.util.*

data class AddImageResponse(
    val id: UUID,
) : Response {
    companion object {
        fun from(id: UUID): AddImageResponse {
            return AddImageResponse(id)
        }
    }
}