package app.gym.api.response

import app.gym.domain.franchise.Franchise

data class GetFranchiseListResponse(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(franchise: Franchise): GetFranchiseListResponse {
            return GetFranchiseListResponse(franchise.id!!, franchise.name)
        }
    }

}
