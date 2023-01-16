package app.gym.api.response

import app.gym.domain.franchise.Franchise

data class GetFranchiseListResponse(
    val franchises: List<SimpleFranchise>,
) {

    data class SimpleFranchise(
        val id: Long,
        val name: String,
    )

    companion object {
        fun from(franchises: List<Franchise>): GetFranchiseListResponse {
            return GetFranchiseListResponse(franchises.map { SimpleFranchise(it.id!!, it.name) })
        }
    }
}