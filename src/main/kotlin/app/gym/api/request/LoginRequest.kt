package app.gym.api.request

import app.gym.domain.member.LoginCommand

class LoginRequest(
    val email: String,
    val password: String
) {
    fun toCommand(): LoginCommand {
        return LoginCommand(email, password)
    }
}
