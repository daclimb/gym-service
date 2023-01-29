package app.gym.api.response

import app.gym.domain.franchise.Franchise


data class GetFranchiseResponse(
    val name: String,
    val description: String,
    val relatedGyms: List<RelatedGym>,
) {

    data class RelatedGym(
        val id: Long,
        val name: String,
    )

    companion object {
        fun from(franchise: Franchise): GetFranchiseResponse {
            val relatedGyms = franchise.gyms.map { RelatedGym(it.id!!, it.name) }
            return GetFranchiseResponse(franchise.name, franchise.description, relatedGyms)
        }
    }
}

