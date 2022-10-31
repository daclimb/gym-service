package app.gym.api

import app.gym.api.request.AddGymRequest
import app.gym.api.request.UpdateGymRequest
import app.gym.api.response.AddImageResponse
import app.gym.api.response.GetGymResponse
import app.gym.api.response.GetSimpleGymResponse
import app.gym.api.response.UpdateGymResponse
import app.gym.domain.gym.GymNotFoundException
import app.gym.domain.gym.GymService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/gym")
class GymController(
    private val gymService: GymService
) {

    @GetMapping("/{id}")
    fun getGym(
        @PathVariable id: Long
    ): ResponseEntity<GetGymResponse> {
        return try {
            val gym = gymService.getGym(id)
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
    fun addGym(
        @RequestBody request: AddGymRequest
    ): ResponseEntity<Any> {
        val command = request.toCommand()
        gymService.addGym(command)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @DeleteMapping("/{id}")
    fun deleteGym(
        @PathVariable id: Long
    ): ResponseEntity<Any> {
        return try {
            gymService.deleteGym(id)
            ResponseEntity.ok().build()
        } catch (e: GymNotFoundException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateGym(
        @PathVariable id: Long,
        @RequestBody request: UpdateGymRequest
    ): ResponseEntity<UpdateGymResponse> {
        return try {
            val command = request.toCommand(id)
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