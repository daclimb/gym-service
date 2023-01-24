package app.gym.api.response

data class AddImageResponse(
    val id: String,
) {
    companion object {
        fun from(id: String): AddImageResponse {
            return AddImageResponse(id)
        }
    }
}