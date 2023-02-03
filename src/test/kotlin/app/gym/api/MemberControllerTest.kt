package app.gym.api

import app.gym.api.controller.MemberController
import app.gym.config.SecurityConfig
import app.gym.domain.member.*
import app.gym.security.JwtTokenHandler
import app.gym.security.UserPrincipal
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.result.isEqualTo
import restdocs.RestdocsType
import restdocs.andDocument
import java.util.stream.Stream
import javax.servlet.http.Cookie

@WebMvcTest(value = [MemberController::class])
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var memberService: MemberService

    @MockkBean
    private lateinit var jwtTokenHandler: JwtTokenHandler

    @ParameterizedTest
    @MethodSource("emailProvider")
    fun `Should return 400 with invalid email and 200 with valid email when signup`(
        email: String?,
        expectedStatusCode: HttpStatus,
    ) {
        val request = mapOf(
            "email" to email,
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        every { memberService.signup(any()) } returns Unit

        val result = mvc.perform(
            post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        result.andExpect(status().isEqualTo(expectedStatusCode.value()))

        result.andDocument("MemberSignup") {
            tags = setOf("Member")
            request("SignupRequest") {
                field("email") {
                    type = RestdocsType.STRING
                    description = "email address of the signup form"
                }
                field("password") {
                    type = RestdocsType.STRING
                    description = "password of the signup form"
                }
                field("name") {
                    type = RestdocsType.STRING
                    description = "name of the signup form"
                }
            }
        }
    }

    private fun emailProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of("invalid_email", HttpStatus.BAD_REQUEST),
            Arguments.of("valid@email.com", HttpStatus.OK)
        )
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    fun `Should return 400 with invalid password and 200 with valid password when signup`(
        password: String?,
        expectedStatusCode: HttpStatus,
    ) {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to password,
            "name" to "name"
        )

        every { memberService.signup(any()) } returns Unit

        val mapper = jacksonObjectMapper()

        mvc.perform(
            post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
            .andExpect(status().isEqualTo(expectedStatusCode.value()))
    }

    @ParameterizedTest
    @MethodSource("nameProvider")
    fun `Should return 400 with invalid name and 200 with valid name when sign up`(
        name: String?,
        expectedStatusCode: HttpStatus,
    ) {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password",
            "name" to name
        )

        every { memberService.signup(any()) } returns Unit

        val mapper = jacksonObjectMapper()

        mvc.perform(
            post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
            .andExpect(status().isEqualTo(expectedStatusCode.value()))
    }

    @Test
    fun `Should return 400 when signup with existing email`() {
        val request = mapOf(
            "email" to "existing@email.com",
            "password" to "password",
            "name" to "name"
        )

        val mapper = jacksonObjectMapper()

        every { memberService.signup(any()) } throws DuplicatedEmailException()

        mvc.perform(
            post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return 400 when sign in with invalid email`() {
        val request = mapOf(
            "email" to "invalid@email.com",
            "password" to "password"
        )

        val mapper = jacksonObjectMapper()

        every { memberService.login(any()) } throws MemberNotFoundException()

        mvc.perform(
            post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return 400 when sign in with invalid password`() {
        val request = mapOf(
            "email" to "invalid@email.com",
            "password" to "password"
        )

        val mapper = jacksonObjectMapper()

        every { memberService.login(any()) } throws PasswordNotMatchedException()

        mvc.perform(
            post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Should return 200 and JWT token when login`() {
        val mapper = jacksonObjectMapper()
        val token = "valid_token"
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password"
        )

        every { memberService.login(any()) } returns token

        val result = mvc.perform(
            post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )
        result
            .andExpect(status().isOk)
            .andExpect(cookie().value("jwt", token))


        result.andDocument("MemberLogin") {
            tags = setOf("Member")
            request("LoginRequest") {
                field("email") {
                    type = RestdocsType.STRING
                    description = "email address of the login form"
                }
                field("password") {
                    type = RestdocsType.STRING
                    description = "password of the login form"
                }
            }
        }
    }

    @Test
    fun `Should return 200 and member info when get me`() {
        val email = "valid@email.com"
        val password = "password"
        val name = "name"
        val member = Member(email, password, name)
        val tokenString = "valid_token"
        val memberId = 1L
        val userPrincipal = UserPrincipal.member(memberId)
        every { jwtTokenHandler.createPrincipal(tokenString) } returns userPrincipal
        every { memberService.getMember(memberId) } returns member

        val cookie = Cookie("jwt", tokenString)

        cookie.maxAge = 24 * 60 * 60
        cookie.secure = true
        cookie.isHttpOnly = true

        val result = mvc.perform(
            get("/api/member/me")
                .cookie(cookie)
        )

        result
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(member.name))

        result.andDocument("Me") {
            tags = setOf("Member")
            response("MeResponse") {
                field("name") {
                    type = RestdocsType.STRING
                    description = "name of the member"
                }
            }
        }
    }

    private fun passwordProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("1", HttpStatus.BAD_REQUEST),
            Arguments.of("123", HttpStatus.BAD_REQUEST),
            Arguments.of("123123123123123123123123", HttpStatus.BAD_REQUEST),
            Arguments.of("valid_password", HttpStatus.OK)

        )
    }

    private fun nameProvider(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of(
                "this name is toooooooooooooooooooooooooooooooooo long to create member...",
                HttpStatus.BAD_REQUEST
            ),
            Arguments.of("n", HttpStatus.OK),
            Arguments.of("name", HttpStatus.OK)
        )
    }
}