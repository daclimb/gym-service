package app.gym.api.controller

import app.gym.api.request.LoginRequest
import app.gym.api.response.SimpleSuccessfulResponse
import app.gym.domain.member.AdminService
import app.gym.util.CookieUtils
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
    private val logger = KotlinLogging.logger { }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<SimpleSuccessfulResponse> {
        logger.info { "Admin login request" }
        val command = request.toCommand()
        val token = adminService.login(command)

        val cookie = CookieUtils.create(token)
        response.addCookie(cookie)

        return ResponseEntity.ok().body(SimpleSuccessfulResponse("Success: login"))
    }
}