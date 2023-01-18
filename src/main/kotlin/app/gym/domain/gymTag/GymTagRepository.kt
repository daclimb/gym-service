package app.gym.domain.gymTag

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymTagRepository: JpaRepository<GymTag, Long>