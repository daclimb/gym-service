package app.gym.api

import app.gym.config.SecurityConfig
import app.gym.domain.jwt.JwtProvider
import app.gym.domain.member.*
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParametersBuilder
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.isEqualTo
import java.util.stream.Stream
import javax.servlet.http.Cookie

@WebMvcTest(
    value = [MemberController::class],

)
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MemberControllerTest {

    @Autowired
    lateinit var mvc: MockMvc


    @TestConfiguration
    class Config {
        @Bean
        fun jwtProvider() = mockk<JwtProvider>()

        @Bean
        fun userService(jwtProvider: JwtProvider) = mockk<MemberService>()
    }

    @Autowired
    private lateinit var memberService: MemberService

    @Autowired
    private lateinit var jwtProvider: JwtProvider

    @ParameterizedTest
    @MethodSource("emailProvider")
    fun `Should return 400 with invalid email and 200 with valid email when signup`(
        email: String?,
        expectedStatusCode: HttpStatus
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

        result
            .andExpect(
                status().isEqualTo(expectedStatusCode.value())
            )
            .andDo(
                MockMvcRestDocumentationWrapper.document(
                    "signup",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    resource(
                        ResourceSnippetParametersBuilder()
                            .requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("password"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("name")
                            ).build()
                    )

                )
            )
    }

    private fun emailProvider(): Stream<Arguments> {
        return Stream.of(
//            Arguments.of(null, HttpStatus.BAD_REQUEST),
            Arguments.of("invalid_email", HttpStatus.BAD_REQUEST),
            Arguments.of("valid@email.com", HttpStatus.OK)
        )
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    fun `Should return 400 with invalid password and 200 with valid password when signup`(
        password: String?,
        expectedStatusCode: HttpStatus
    ) {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to password,
            "name" to "name"
        )

        every { memberService.signup(any()) } returns Unit

        val mapper = jacksonObjectMapper()

        mvc.post("/api/member/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(expectedStatusCode.value()) }
        }
    }

    @ParameterizedTest
    @MethodSource("nameProvider")
    fun `Should return 400 with invalid name and 200 with valid name when sign up`(
        name: String?,
        expectedStatusCode: HttpStatus
    ) {
        val request = mapOf(
            "email" to "valid@email.com",
            "password" to "password",
            "name" to name
        )

        every { memberService.signup(any()) } returns Unit

        val mapper = jacksonObjectMapper()

        mvc.post("/api/member/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isEqualTo(expectedStatusCode.value()) }
        }
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

        mvc.post("/api/member/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Should return 400 when sign in with invalid email`() {
        val request = mapOf(
            "email" to "invalid@email.com",
            "password" to "password"
        )

        val mapper = jacksonObjectMapper()

        every { memberService.login(any()) } throws MemberNotFoundException()

        mvc.post("/api/member/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Should return 400 when sign in with invalid password`() {
        val request = mapOf(
            "email" to "invalid@email.com",
            "password" to "password"
        )

        val mapper = jacksonObjectMapper()

        every { memberService.login(any()) } throws PasswordNotMatchedException()

        mvc.post("/api/member/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
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

        mvc.post("/api/member/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            cookie { value("jwt", token) }
        }
    }

    @Test
    @WithMockUser(userId = 1L)
    fun `Should return 200 and member info when get me`() {
        val email = "valid@email.com"
        val password = "password"
        val name = "name"
        val member = Member(email, password, name)
        val token = "JWT token"

        val memberId = 1L

        every { jwtProvider.parseMemberId(token) } returns memberId
        every { memberService.getMember(memberId) } returns member

        val cookie = Cookie("jwt", token)

        cookie.maxAge = 24 * 60 * 60

        cookie.secure = true
        cookie.isHttpOnly = true

        mvc.get("/api/member/me") {
            cookie(cookie)
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value(member.name) }
                jsonPath("$.logoUrl") { value(member.logoUrl) }
            }
            .andDo { print() }
    }

    fun passwordProvider(): Stream<Arguments> {
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