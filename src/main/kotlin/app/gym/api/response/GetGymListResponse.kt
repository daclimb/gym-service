package app.gym.api.response

import app.gym.domain.gym.Gym

data class GetGymListResponse(
    val gyms: List<SimpleGym>,
) {

    data class SimpleGym(
        val id: Long,
        val name: String,
        val address: String,
        val thumbnail: String?,
    )

    companion object {
        fun from(gyms: List<Gym>): GetGymListResponse {
            return GetGymListResponse(gyms.map {
                val thumbnail: String? =
                    if (it.images.size == 0) {
                        null
                    } else {
                        it.images[0].uuid
                    }

                SimpleGym(
                    it.id!!,
                    it.name,
                    it.address,
                    thumbnail
                )
            })
        }
    }
}