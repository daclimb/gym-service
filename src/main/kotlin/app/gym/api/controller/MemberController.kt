package app.gym.api.controller

import app.gym.api.request.LoginRequest
import app.gym.api.request.SignupRequest
import app.gym.api.response.MeResponse
import app.gym.api.response.SimpleSuccessfulResponse
import app.gym.domain.member.*
import app.gym.security.UserPrincipal
import app.gym.util.CookieUtils
import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/api/member")
class MemberController(
    private val memberService: MemberService,
) {
    private val logger = KotlinLogging.logger { }

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignupRequest): ResponseEntity<Any> {
        logger.info { "Signup request" }
        memberService.signup(request.toCommand())
        return ResponseEntity.ok().body(SimpleSuccessfulResponse("Success: signup"))
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest
    ): ResponseEntity<SimpleSuccessfulResponse> {
        logger.info { "Login request" }
        val command = request.toCommand()
        val token = memberService.login(command)
        val cookie = CookieUtils.createCookie(token)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(SimpleSuccessfulResponse("Success: login"))
    }

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal principal: UserPrincipal): ResponseEntity<MeResponse> {
        logger.info { "Get me request" }
        val member = memberService.getMember(principal.memberId!!)
        val response = MeResponse.from(member)
        return ResponseEntity.ok(response)
    }
}