package app.gym.domain.franchise

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FranchiseRepository : JpaRepository<Franchise, Long>