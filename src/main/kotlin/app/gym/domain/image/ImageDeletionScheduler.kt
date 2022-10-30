package app.gym.domain.image

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@EnableScheduling
class ImageDeletionScheduler(
    @Value("\${scheduler.image-deletion.time}")
    private val standardDayTerm: Long,
    private val imageRepository: ImageRepository,
    private val imageStorage: ImageStorage
) {

    @Scheduled(cron = "0 0 0 * * ?")
    fun executeScheduler() {
        val currentDateTime = LocalDateTime.now()
        val standardDateTime = currentDateTime.plusDays(-standardDayTerm)
        val images = imageRepository.findByGymNullAndCreatedAtLessThan(standardDateTime)
        imageRepository.deleteAll(images)
        val ids = images.map { it.uuid!! }
        imageStorage.deleteWithUuids(ids)
    }
}
