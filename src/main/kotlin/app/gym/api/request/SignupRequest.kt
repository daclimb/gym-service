package app.gym.api.request

import app.gym.domain.member.SignupCommand
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.Email

data class SignupRequest(
    @field:Email
    val email: String,
    @field:Length(min=8, max=16)
    val password: String,
    @field:Length(min=1, max=50)
    val name: String
) {
    fun toCommand(): SignupCommand {
        return SignupCommand(email, password, name)
    }
}
