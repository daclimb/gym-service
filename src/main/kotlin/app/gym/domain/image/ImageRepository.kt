package app.gym.domain.image

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ImageRepository: JpaRepository<Image, String> {
    fun findByUuidIn(uuids: List<String>): List<Image>
    fun countAllByUuidIn(uuids: List<String>): Int

    fun findByGymNullAndCreatedAtLessThan(createdAt: LocalDateTime): List<Image>
}