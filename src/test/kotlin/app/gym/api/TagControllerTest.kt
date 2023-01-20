package app.gym.api

import app.gym.api.controller.TagController
import app.gym.api.request.AddTagRequest
import app.gym.config.SecurityConfig
import app.gym.domain.tag.Tag
import app.gym.domain.tag.TagDuplicatedException
import app.gym.domain.tag.TagNotExistsException
import app.gym.domain.tag.TagService
import app.gym.security.JwtAuthenticationFilter
import app.gym.util.JsonUtils
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import restdocs.RestdocsType
import restdocs.andDocument2

@WebMvcTest(
    controllers = [TagController::class],
    excludeFilters = [
        ComponentScan.Filter(
            classes = [JwtAuthenticationFilter::class],
            type = FilterType.ASSIGNABLE_TYPE
        )]
)
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
internal class TagControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Autowired
    @MockkBean
    private lateinit var tagService: TagService

    @Test
    fun `Should return status code 201 when add tag`() {
        val request = AddTagRequest("new tag")
        val content = JsonUtils.toJson(request)

        every { tagService.addTag(any()) } returns Unit

        val result = mvc.perform(
            post("/api/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect {
            status().isCreated
        }

        result.andDocument2("AddTag") {
            tags = setOf("Tag")

            request("AddTagRequest") {
                field("tag") {
                    type = RestdocsType.STRING
                    description = "string of the tag"
                }
            }
        }
    }

    @Test
    fun `Should return status code 400 when add duplicated tag`() {
        val request = AddTagRequest("new tag")
        val content = JsonUtils.toJson(request)

        every { tagService.addTag(any()) } throws TagDuplicatedException()

        val result = mvc.perform(
            post("/api/tag")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        )

        result.andExpect {
            status().isBadRequest
        }
    }

    @ParameterizedTest
    @ValueSource(longs = [0L, 3L])
    fun `Should return status code 200 when get tag list`(length: Long) {
        val tags = mutableListOf<Tag>()
        for (i: Long in 1L..length)
            tags.add(Tag(i, "new tag"))
        every { tagService.getTags() } returns tags

        val result = mvc.perform(
            get("/api/tag")
        )

        result.andExpect {
            status().isOk
            jsonPath("$.tags.length()").value(length)
        }

        if (length == 3L) {
            result.andDocument2("GetTagList") {
                this.tags = setOf("Tag")

                response("GetTagListResponse") {
                    array("tags") {
                        field("id") {
                            type = RestdocsType.NUMBER
                            description = "id of the tag"
                        }
                        field("tag") {
                            type = RestdocsType.STRING
                            description = "string of the tag"
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Should return status code 200 when delete tag`() {
        every { tagService.deleteTag(any()) } returns Unit

        val result = mvc.perform(
            delete("/api/tag/{tagId}", 1L)
        )

        result.andExpect {
            status().isOk
        }

        result.andDocument2("DeleteTag") {
            tags = setOf("Tag")

            request {
                pathParam("tagId") {
                    type = RestdocsType.NUMBER
                    description = "id of the tag"
                }
            }
        }
    }

    @Test
    fun `Should return status code 400 when delete not existing tag`() {
        every { tagService.deleteTag(any()) } throws TagNotExistsException()

        val result = mvc.perform(
            delete("/api/tag/{tagId}", 1L)
        )

        result.andExpect {
            status().isBadRequest
        }
    }
}