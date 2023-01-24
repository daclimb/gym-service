package app.gym.api.response

import app.gym.domain.tag.Tag

data class GetTagListResponse(
    val tags: List<SimpleTag>
) {

    data class SimpleTag(
        val id: Long,
        val tag: String
    )
    companion object {
        fun from(tags: List<Tag>): GetTagListResponse {
            return GetTagListResponse(tags.map{ SimpleTag(it.id!!, it.tagString) })
        }
    }
}