package app.gym.domain.franchise

import org.springframework.stereotype.Service

@Service
class FranchiseService(
    private val franchiseRepository: FranchiseRepository
) {
    fun addFranchise(command: AddFranchiseCommand): Long {
        val franchise = Franchise()
        franchise.updateDetails(command.name, command.description)
        return franchiseRepository.save(franchise).id!!
    }

    fun getFranchise(id: Long): Franchise {
        return franchiseRepository.findById(id).get()
    }

    fun getFranchiseList(): List<Franchise> {
        return franchiseRepository.findAll()
    }

    fun deleteFranchise(id: Long) {
        franchiseRepository.deleteById(id)
    }

    fun updateFranchise(command: UpdateFranchiseCommand) {
        val franchise = franchiseRepository.findById(command.id).get()
        franchise.updateDetails(command.name, command.description)
    }
}