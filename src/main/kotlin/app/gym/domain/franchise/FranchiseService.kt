package app.gym.domain.franchise

import org.springframework.stereotype.Service

@Service
class FranchiseService(
    private val franchiseRepository: FranchiseRepository
) {
    fun addFranchise(name: String, description: String) {
        val franchise = Franchise()
        franchise.update(name, description)
        franchiseRepository.save(franchise)
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
        franchise.update(command.name, command.description)
    }
}