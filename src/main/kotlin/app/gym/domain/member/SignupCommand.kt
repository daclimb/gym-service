package app.user.domain.member

class SignupCommand(
    val email: String,
    val password: String,
    val name: String
)
