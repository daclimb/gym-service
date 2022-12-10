package app.user.api.response

import app.user.domain.member.Member

class MeResponse(
    val name: String,
    val logoUrl: String?
) {
    companion object {
        fun from(member: Member): MeResponse {
            return MeResponse(member.name, member.logoUrl)
        }
    }
}