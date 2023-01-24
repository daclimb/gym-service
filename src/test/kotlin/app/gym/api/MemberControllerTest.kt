package app.gym.api

import app.gym.api.controller.MemberController
import app.gym.config.SecurityConfig
import app.gym.domain.member.*
import app.gym.security.JwtTokenHandler
import app.gym.security.UserPrincipal
import com.epages.restdocs.apispec.Schema
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
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.result.isEqualTo
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
    @MethodSource("signupFormProvider")
    fun `Should return 400 with invalid email and 200 with valid email when signup`(
        email: String?,
        password: String?,
        name: String?,
        expectedStatusCode: HttpStatus,
    ) {
        val request = mapOf(
            "email" to email,
            "password" to password,
            "name" to name
        )
        val mapper = jacksonObjectMapper()

        every { memberService.signup(any()) } returns Unit

        val result = mvc.perform(
            post("/api/member/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        )

        result.andExpect(
            status().isEqualTo(expectedStatusCode.value())
        )
        if (expectedStatusCode == HttpStatus.OK) {
            result.andDocument("Signup") {
                tag("Member")
                requestSchema(Schema("SignupRequest"))
                requestFields(
                    fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                    fieldWithPath("password").type(JsonFieldType.STRING).description("password"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("name")
                )
            }
        }
    }

    private fun signupFormProvider(): Stream<Arguments> {
        val validEmail = "valid@email.com"
        val validPassword = "valid_password"
        val validName = "valid_name"
        return Stream.of(
            Arguments.of(null, validPassword, validName, HttpStatus.BAD_REQUEST),
            Arguments.of("invalid_email", validPassword, validName, HttpStatus.BAD_REQUEST),

            Arguments.of(validEmail, null, validName, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, "1", validName, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, "123", validName, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, "123123123123123123123123", validName, HttpStatus.BAD_REQUEST),

            Arguments.of(validEmail, validPassword, null, HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, validPassword, "this name is toooooooooooooooooooooooooooooo long to create member...", HttpStatus.BAD_REQUEST),
            Arguments.of(validEmail, validPassword, validName, HttpStatus.CREATED),
        )
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
        val token = "JWT token"
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password"
        )

        every { memberService.login(any()) } returns token

        val result = mvc.perform(
            post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
        ).andExpect {
            status().isOk
            cookie().value("jwt", token)
        }

        result.andDocument("Login") {
            tag("Member")
            requestSchema(Schema("LoginRequest"))
            requestFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("email address"),
                fieldWithPath("password").type(JsonFieldType.STRING).description("password")
            )
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
        ).andExpect {
            status().isOk
            jsonPath("$.name").value(member.name)
        }
//            .andExpect(jsonPath("$.logoUrl").value(member.logoUrl))

        result.andDocument("Me") {
            tag("Member")
            responseSchema(Schema("MeRequest"))
            responseFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the member")
//                fieldWithPath("logoUrl").type(JsonFieldType.STRING).description("logo url of the member")
            )
        }
    }

}