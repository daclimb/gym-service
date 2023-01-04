package app.gym.api.response

import app.gym.domain.franchise.Franchise

data class RelatedGym(
    val id: Long,
    val name: String
)

data class GetFranchiseResponse(
    val id: Long,
    val name: String,
    val description: String,
    val RelatedGyms: List<RelatedGym>
) {
    companion object {
        fun from(franchise: Franchise): GetFranchiseResponse {
            val relatedGyms = franchise.gyms.map { RelatedGym(it.id!!, it.name) }
            return GetFranchiseResponse(franchise.id!!, franchise.name, franchise.description, relatedGyms)
        }
    }
}

