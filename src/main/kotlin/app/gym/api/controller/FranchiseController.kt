package app.gym.api.controller

import app.gym.api.request.AddFranchiseRequest
import app.gym.api.request.UpdateFranchiseRequest
import app.gym.api.response.*
import app.gym.domain.franchise.FranchiseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/franchise")
class FranchiseController(
    private val franchiseService: FranchiseService,
) {
    @GetMapping("/{franchiseId}")
    fun getFranchise(@PathVariable franchiseId: Long): ResponseEntity<GetFranchiseResponse> {
        val franchise = franchiseService.getFranchise(franchiseId)
        val response = GetFranchiseResponse.from(franchise)
        return ResponseEntity.ok().body(response)
    }

    @GetMapping
    fun getFranchiseList(): ResponseEntity<GetFranchiseListResponse> {
        val franchiseList = franchiseService.getFranchiseList()
        val response = GetFranchiseListResponse.from(franchiseList)
        return ResponseEntity.ok().body(response)
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun addFranchise(@RequestBody request: AddFranchiseRequest): ResponseEntity<AddFranchiseResponse> {
        val command = request.toCommand()
        val franchiseId = franchiseService.addFranchise(command)
        val response = AddFranchiseResponse(franchiseId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PutMapping("/{franchiseId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun updateFranchise(
        @PathVariable franchiseId: Long,
        @RequestBody request: UpdateFranchiseRequest,
    ): ResponseEntity<SimpleSuccessfulResponse> {
        val command = request.toCommand(franchiseId)
        franchiseService.updateFranchise(command)
        val response = SimpleSuccessfulResponse("Success: update franchise")
        return ResponseEntity.ok().body(response)
    }

    @DeleteMapping("/{franchiseId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun deleteFranchise(@PathVariable franchiseId: Long): ResponseEntity<SimpleSuccessfulResponse> {
        franchiseService.deleteFranchise(franchiseId)
        val response = SimpleSuccessfulResponse("Success: delete franchise")
        return ResponseEntity.ok().body(response)
    }
}