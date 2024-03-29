package app.gym.api

import app.gym.api.controller.FranchiseController
import app.gym.api.request.AddFranchiseRequest
import app.gym.api.request.UpdateFranchiseRequest
import app.gym.config.SecurityConfig
import app.gym.domain.franchise.FranchiseService
import app.gym.domain.member.UserRole
import app.gym.domain.member.WithCustomMockUser
import app.gym.security.JwtAuthenticationFilter
import app.gym.util.JsonUtils
import app.gym.utils.TestDataGenerator
import restdocs.andDocument
import com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName
import com.epages.restdocs.apispec.Schema
import com.epages.restdocs.apispec.SimpleType
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [FranchiseController::class],
    excludeFilters = [
        ComponentScan.Filter(
            classes = [JwtAuthenticationFilter::class],
            type = FilterType.ASSIGNABLE_TYPE
        )]
)
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
class FranchiseControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean
    private lateinit var franchiseService: FranchiseService

    @ParameterizedTest
    @ValueSource(longs = [0L, 3L])
    fun `Should return status code 200 and franchise list when get franchise list`(length: Long) {
        val franchiseList = TestDataGenerator.franchiseList(length)
        every { franchiseService.getFranchiseList() } returns franchiseList

        val result = mvc.perform(get("/api/franchise"))

        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.franchises.length()").value(length))

        if (length == 3L) {
            result.andDocument("GetFranchiseList") {
                tag("Franchise")
                responseSchema(Schema("GetFranchiseList"))
                responseFields(
                    fieldWithPath("franchises[].id").type(JsonFieldType.NUMBER).description("id of the franchise"),
                    fieldWithPath("franchises[].name").type(JsonFieldType.STRING).description("name of the franchise")
                )
            }
        }
    }

    @Test
    fun `Should return status code 200 and franchise details when get franchise`() {
        val franchise = TestDataGenerator.franchise(1L)

        every { franchiseService.getFranchise(any()) } returns franchise

        val result = mvc.perform(get("/api/franchise/{franchiseId}", 1L))

        result.andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(franchise.id))
            .andExpect(jsonPath("$.name").value(franchise.name))
            .andExpect(jsonPath("$.description").value(franchise.description))

        result.andDocument("GetFranchise") {
            tag("Franchise")
            pathParameters(
                parameterWithName("franchiseId").type(SimpleType.INTEGER).description("id if the franchise")
            )
            responseSchema(Schema("getFranchise"))
            responseFields(
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("id of the franchise"),
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the franchise"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description of the franchise"),
                fieldWithPath("relatedGyms").type(JsonFieldType.ARRAY).description("gyms of the franchise"),
                fieldWithPath("relatedGyms[].id").type(JsonFieldType.NUMBER).description("id of the related gym"),
                fieldWithPath("relatedGyms[].name").type(JsonFieldType.STRING).description("name of the related gym")
            )
        }
    }

    @Test
    @WithCustomMockUser(userRole = UserRole.Admin)
    fun `Should return status code 201 when add franchise`() {
        val request = AddFranchiseRequest("name", "description")
        val content = JsonUtils.toJson(request)

        every { franchiseService.addFranchise(any()) } returns 1L

        val result = mvc.perform(
            post("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isCreated)

        result.andDocument("AddFranchise") {
            tag("Franchise")
            requestSchema(Schema("AddFranchiseRequest"))
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the franchise"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description of the franchise")
            )
        }
    }

    @Test
    @WithCustomMockUser(userRole = UserRole.Admin)
    fun `Should return status code 200 when update franchise`() {
        val request = UpdateFranchiseRequest("name", "description")
        val content = JsonUtils.toJson(request)

        every { franchiseService.updateFranchise(any()) } returns Unit

        val result = mvc.perform(
            put("/api/franchise/{franchiseId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect(status().isOk)

        result.andDocument("UpdateFranchise") {
            tag("Franchise")
            pathParameters(
                parameterWithName("franchiseId").type(SimpleType.INTEGER).description("id of the franchise")
            )
            requestSchema(Schema("UpdateFranchiseRequest"))
            requestFields(
                fieldWithPath("name").type(JsonFieldType.STRING).description("name of the franchise"),
                fieldWithPath("description").type(JsonFieldType.STRING).description("description of the franchise")
            )
        }
    }

    @Test
    @WithCustomMockUser(userRole = UserRole.Admin)
    fun `Should return status code 200 when delete franchise`() {

        every { franchiseService.deleteFranchise(any()) } returns Unit

        val result = mvc.perform(delete("/api/franchise/{franchiseId}", 1L))

        result.andExpect(status().isOk)

        result.andDocument("DeleteFranchise") {
            tag("Franchise")
            pathParameters(
                parameterWithName("franchiseId").type(SimpleType.INTEGER).description("id of the franchise")
            )
        }
    }
}