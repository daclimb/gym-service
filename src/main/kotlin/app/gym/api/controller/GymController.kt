package app.gym.api.controller

import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.*
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.GymService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/gym")
class GymController(
    private val gymService: GymService
) {
    @GetMapping("/{gymId}")
    fun getGym(
        @PathVariable gymId: Long
    ): ResponseEntity<Response> {
        return try {
            val gym = gymService.getGym(gymId)
            val response = GetGymResponse.from(gym)
            ResponseEntity.ok(response)
        } catch (e: GymNotFoundException) {
            val response = ClientErrorResponse(e.message)
            ResponseEntity.badRequest().body(response)
        }
    }

    @GetMapping
    fun getGymList(): ResponseEntity<Response> {
        val gyms = gymService.getGyms()
        val responses = GetGymListResponse.from(gyms)
        return ResponseEntity.ok(responses)
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun addGym(
        @RequestBody request: AddGymRequest
    ): ResponseEntity<Response> {
        val command = request.toCommand()
        val gymId = gymService.addGym(command)
        val response = AddGymResponse(gymId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/{gymId}")
    fun deleteGym(
        @PathVariable gymId: Long
    ): ResponseEntity<Response> {
        return try {
            gymService.deleteGym(gymId)
            val response = SuccessfulResponse("Success: delete gym")
            ResponseEntity.ok().body(response)
        } catch (e: GymNotFoundException) {
            val response = ClientErrorResponse(e.message)
            ResponseEntity.badRequest().body(response)
        }
    }

    @PutMapping("/{gymId}")
    fun updateGym(
        @PathVariable gymId: Long,
        @RequestBody request: UpdateGymRequest
    ): ResponseEntity<Response> {
        return try {
            val command = request.toCommand(gymId)
            gymService.updateGym(command)
            val response = SuccessfulResponse("Success: update gym")
            ResponseEntity.ok().body(response)
        } catch (e: GymNotFoundException) {
            val response = ClientErrorResponse(e.message)
            ResponseEntity.badRequest().body(response)
        }
    }

    @PostMapping("/image")
    fun addImage(@RequestParam image: MultipartFile): ResponseEntity<Response> {
        val uuid = gymService.addImage(image)
        val response = AddImageResponse.from(uuid)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}