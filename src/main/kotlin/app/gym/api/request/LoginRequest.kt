package app.user.api.request

import app.user.domain.member.LoginCommand

class LoginRequest(
    val email: String,
    val password: String
) {
    fun toCommand(): LoginCommand {
        return LoginCommand(email, password)
    }
}
