package app.gym.api.request

import app.gym.domain.member.LoginCommand

data class LoginRequest(
    val email: String,
    val password: String
) {
    fun toCommand(): LoginCommand {
        return LoginCommand(email, password)
    }
}
