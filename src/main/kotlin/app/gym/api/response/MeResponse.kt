package app.gym.api.response

import app.gym.domain.member.Member

data class MeResponse(
    val name: String,
//    val logoUrl: String?
) {
    companion object {
        fun from(member: Member): MeResponse {
            return MeResponse(member.name)
//            return MeResponse(member.name, member.logoUrl)
        }
    }
}