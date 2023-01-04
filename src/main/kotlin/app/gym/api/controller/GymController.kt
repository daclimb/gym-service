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
    ): ResponseEntity<GetGymResponse> {
        return try {
            val gym = gymService.getGym(gymId)
            ResponseEntity.ok(GetGymResponse.from(gym))
        } catch (e: GymNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping
    fun getGymList(): ResponseEntity<List<GetSimpleGymResponse>> {
        val gyms = gymService.getGyms()
        val responses = gyms.map(GetSimpleGymResponse::from)
        return ResponseEntity.ok(responses)
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun addGym(
        @RequestBody request: AddGymRequest
    ): ResponseEntity<Any> {
        val command = request.toCommand()
        val gymId = gymService.addGym(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(AddGymResponse(gymId))
    }

    @DeleteMapping("/{gymId}")
    fun deleteGym(
        @PathVariable gymId: Long
    ): ResponseEntity<Any> {
        return try {
            gymService.deleteGym(gymId)
            ResponseEntity.ok().build()
        } catch (e: GymNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{gymId}")
    fun updateGym(
        @PathVariable gymId: Long,
        @RequestBody request: UpdateGymRequest
    ): ResponseEntity<Any> {
        return try {
            val command = request.toCommand(gymId)
            gymService.updateGym(command)
            ResponseEntity.ok().build()
        } catch (e: GymNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/image")
    fun addImage(@RequestParam image: MultipartFile): ResponseEntity<AddImageResponse> {
        val uuid = gymService.addImage(image)
        return ResponseEntity.status(HttpStatus.CREATED).body(AddImageResponse.from(uuid))
    }
}