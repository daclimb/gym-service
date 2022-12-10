package app.user.api

import app.user.api.request.LoginRequest
import app.user.api.request.SignupRequest
import app.user.api.response.MeResponse
import app.user.domain.member.*
import app.user.util.CookieUtils
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid



@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberService: MemberService,
) {
    private val logger = KotlinLogging.logger {  }

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Any> {
        logger.info { "Signup request" }
        return try {
            memberService.signup(request.toCommand())
            ResponseEntity.ok().build()
        } catch (e: DuplicatedEmailException) {
            logger.info { "Signup failed" }
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<Any> {
        logger.info { "Login request" }
        val command = request.toCommand()
        return try {
            val token = memberService.login(command)

            val cookie = CookieUtils.create(token)
            response.addCookie(cookie)

            ResponseEntity.ok().build()
        } catch (e: MemberNotFoundException) {
            logger.info { "Login failed" }
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        } catch (e: PasswordNotMatchedException) {
            logger.info { "Login failed" }
            logger.debug { "$e" }
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal principal: MemberPrincipal): ResponseEntity<MeResponse> {
        logger.info { "Get me request" }
        return try {
            val member = memberService.getMember(principal.memberId)
            val response = MeResponse.from(member)
            ResponseEntity.ok(response)
        } catch (e: MemberNotFoundException) {
            logger.error { " $e: JWT token based authentication must not throw this exception." }
            ResponseEntity.internalServerError().build()
        }
    }
}