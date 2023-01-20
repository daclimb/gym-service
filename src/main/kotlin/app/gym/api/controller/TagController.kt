package app.gym.api.controller

import app.gym.api.request.AddTagRequest
import app.gym.api.response.GetTagListResponse
import app.gym.api.response.SimpleSuccessfulResponse
import app.gym.domain.tag.TagService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tag")
class TagController(
    private val tagService: TagService
) {
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun addTag(@RequestBody request: AddTagRequest): ResponseEntity<SimpleSuccessfulResponse> {
        val command = request.toCommand()
        tagService.addTag(command)
        return ResponseEntity.ok().body(SimpleSuccessfulResponse("Success: add tag"))
    }

    @GetMapping
    fun getTags(): ResponseEntity<GetTagListResponse> {
        val tags = tagService.getTags()
        val response = GetTagListResponse.from(tags)
        return ResponseEntity.ok().body(response)
    }

    @DeleteMapping("/{tagId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun deleteTag(@PathVariable tagId: Long): ResponseEntity<SimpleSuccessfulResponse> {
        tagService.deleteTag(tagId)
        return ResponseEntity.ok().body(SimpleSuccessfulResponse("Success: delete tag"))
    }
}