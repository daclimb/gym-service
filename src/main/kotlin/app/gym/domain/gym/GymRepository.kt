package app.gym.domain.gym

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymRepository: JpaRepository<Gym, Long>