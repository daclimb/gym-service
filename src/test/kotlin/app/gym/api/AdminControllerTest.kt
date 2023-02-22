package app.gym.api

import app.gym.api.controller.AdminController
import app.gym.api.request.LoginRequest
import app.gym.config.SecurityConfig
import app.gym.domain.member.AdminService
import app.gym.security.JwtAuthenticationFilter
import app.gym.util.JsonUtils
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import restdocs.RestdocsType
import restdocs.andDocument2

@WebMvcTest(
    controllers = [AdminController::class],
    excludeFilters = [
        ComponentScan.Filter(
            classes = [JwtAuthenticationFilter::class],
            type = FilterType.ASSIGNABLE_TYPE
        )]
)
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
class AdminControllerTest {

    @MockkBean
    lateinit var adminService: AdminService

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `Should return status code 200 when login`() {
        val request = LoginRequest("valid@email.com", "valid_password")
        val content = JsonUtils.toJson(request)
        val token = "valid_token"

        every { adminService.login(any()) } returns token

        val result = mvc.perform(
            post("/api/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isOk)

        result.andDocument2("AdminLogin") {
            tags = setOf("admin")

            request("LoginRequest") {
                field("email") {
                    type = RestdocsType.STRING
                    description = "email address of login form"
                }
                field("password") {
                    type = RestdocsType.STRING
                    description = "password of login form"
                }
            }
        }
    }
}