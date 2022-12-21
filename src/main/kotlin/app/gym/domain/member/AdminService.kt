package app.gym.domain.member

import app.gym.config.JwtProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

class EmailOrPasswordNotMatchedException: RuntimeException()

@Service
class AdminService(
    @Value("\${admin.email}")
    private val email: String,
    @Value("\${admin.password}")
    private val password: String,
    private val jwtProvider: JwtProvider
    ) {
    fun login(command: LoginCommand): String {
        if (command.email != email || command.password != password) {
            throw EmailOrPasswordNotMatchedException()
        }
        val token = jwtProvider.generateAdminJwtToken()
        logger.info { "Admin JWT token published" }
        return token
    }
}