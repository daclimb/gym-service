package app.gym.api.controller

import app.gym.api.request.AddFranchiseRequest
import app.gym.api.request.UpdateFranchiseRequest
import app.gym.api.response.AddFranchiseResponse
import app.gym.api.response.GetFranchiseListResponse
import app.gym.api.response.GetFranchiseResponse
import app.gym.domain.franchise.FranchiseService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/franchise")
class FranchiseController(
    private val franchiseService: FranchiseService
) {
    @GetMapping("/{franchiseId}")
    fun getFranchise(@PathVariable franchiseId: Long): ResponseEntity<GetFranchiseResponse> {
        val franchise = franchiseService.getFranchise(franchiseId)
        val response = GetFranchiseResponse.from(franchise)
        return ResponseEntity.ok().body(response)
    }

    @GetMapping
    fun getFranchiseList(): ResponseEntity<List<GetFranchiseListResponse>> {
        val franchiseList = franchiseService.getFranchiseList()
        val response = franchiseList.map {
            GetFranchiseListResponse.from(it)
        }
        return ResponseEntity.ok().body(response)
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun addFranchise(@RequestBody request: AddFranchiseRequest): ResponseEntity<AddFranchiseResponse> {
        val command = request.toCommand()
        val franchiseId = franchiseService.addFranchise(command)
        return ResponseEntity.status(HttpStatus.CREATED).body(AddFranchiseResponse(franchiseId))
    }

    @PutMapping("/{franchiseId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun updateFranchise(@PathVariable franchiseId: Long, @RequestBody request: UpdateFranchiseRequest): ResponseEntity<Any> {
        val command = request.toCommand(franchiseId)
        franchiseService.updateFranchise(command)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{franchiseId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun deleteFranchise(@PathVariable franchiseId: Long): ResponseEntity<Any> {
        franchiseService.deleteFranchise(franchiseId)
        return ResponseEntity.ok().build()
    }
}