package app.user.api

import app.user.api.request.LoginRequest
import app.user.domain.member.AdminService
import app.user.domain.member.EmailOrPasswordNotMatchedException
import app.user.util.CookieUtils
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val adminService: AdminService,
) {
    private val logger = KotlinLogging.logger {  }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<Any>{
        logger.info { "Admin login request" }
        val command = request.toCommand()
        return try {
            val token = adminService.login(command)

            val cookie = CookieUtils.create(token)
            response.addCookie(cookie)

            ResponseEntity.ok().build()
        } catch (e: EmailOrPasswordNotMatchedException) {
            logger.info { "Login failed" }
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        }
    }
}