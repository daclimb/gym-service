package app.gym.api

import app.gym.api.controller.AdminController
import app.gym.config.SecurityConfig
import app.gym.domain.member.AdminService
import app.gym.security.JwtAuthenticationFilter
import com.ninjasquad.springmockk.MockkBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc

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

//    @Test
//    fun `Should return response with jwt cookie when logged in`() {
//        mvc.post("/api/admin/login") {
//
//        }
//    }
}